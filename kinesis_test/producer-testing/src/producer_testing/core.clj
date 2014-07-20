(ns producer-testing.core
  (:require [cheshire.core :as json]
            [amazonica.aws.kinesis :as kinesis])   
  (:import java.util.Date)   
  (:gen-class))


;; set credentials to ensure correct endpoint
(def cred {:access-key (System/getenv "AWS_ACCESS_KEY_ID")
           :secret-key (System/getenv "AWS_SECRET_KEY")
           :endpoint   (or (System/getenv "AWS_PIPELINE_REGION") 
                            "us-west-2")})


;; set producer server and stream name
(def aws-server (or (System/getenv "AWS_PRODUCER_SERVER") 
                            "api-usw1a-001"))

(def pipeline-name (or (System/getenv "AWS_PIPELINE_NAME") 
                            "kinesis-api-test"))


;; format log events
(def event-date
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (Date.)))

(defn event-index [x]
  (format "%08d" x))

(defn event-id [x]
  (str event-date "-" aws-server "-" (event-index x)))

(def http-method :get)

(def response
  {:status 200
   :headers {:link "</search?q=hee&group-by=kind&page=1>"}
   :body {:list []
          :track []
          :release []
          :mix []
          :genre []
          :account []
          :best-match nil}})

(def api-event-base
  {:method http-method
   :uri "/search"
   :action "Request"
   :http-server "org.eclipse.jetty.server.HttpInput@3b702644"
   :user "marlboro"
   :ip "127.0.0.1"
   :user-agent ""
   :response response
   :duration "69ms"
   :events ""
   :time-unix (quot (System/currentTimeMillis) 1000)
   :function-times {}})

(defn create-event [event-key]
  (assoc api-event-base :id event-key))
 

;; publish events to aws kinesis
;; check for existing streams promotes sanity and ensures
;; predictable output formatting (otherwise first call 
;; to aws may result in info item placed oddly)
(defn kinesis-publish
   []
   (def streams (:stream-names (kinesis/list-streams cred)))
   (println "")
   (println "hey now, we must produce!")
   (println "streams available:" streams "\n")
   (loop [x 0]
     (when (< x 2000)
       (def event-key (event-id x))
       (def new-event (json/encode (create-event event-key)))
       (println new-event)
       (def put-result
         (kinesis/put-record cred pipeline-name new-event event-key))
       ;; (println put-result)
       (println (str "==> event <" event-key "> published to aws kinesis"))
       (println "")
       (Thread/sleep 250)
       (recur (+ x 1)))))


(defn -main
  [& args]
  (kinesis-publish))
