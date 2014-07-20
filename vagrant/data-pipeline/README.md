data-pipeline environment
=========================

This directory allows you to bring up a development environment for
running an Ubuntu pipeline testing environment within a virtual
machine on your local host.

Requirements
------------

Before experimenting locally, you must have the following items installed
and working:

* [VirtualBox](https://www.virtualbox.org/) >= v4.3.12
* [Vagrant](http://vagrantup.com/) >= v1.6.x
* [Ansible](http://docs.ansible.com/intro_installation.html) >= v1.6.x

If you're using a Mac, it's easiest to just: `brew install ansible`

### Memory Usage

By default, the development VM is configured to use 2046 MB of RAM. If you'd
like to change that, simply export the `$PIPELINE_MEMORY` environment variable
with a string of your desired amount (in MB) before bringing up the machine:

    $ export PIPELINE_MEMORY='4096'
    $ vagrant up
