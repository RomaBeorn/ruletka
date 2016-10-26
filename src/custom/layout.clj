(ns custom.layout
  (:require 
            [clj-jade.core :as jade]))

(jade/configure {:template-dir "resources/public/jade/"
                 :pretty-print true
                 :cache? true})

(defn db-not-conn []
    (jade/render "db_is_not_conn.jade"))
                                              
(defn report []
    (jade/render "report.jade"))

(defn list-of-reports [bugs]
    (jade/render "list-of-reports.jade" {"bugs" bugs }))

(defn home []
    (jade/render "home.jade"))

(defn contacts []
    (jade/render "contacts.jade"))

(defn noncryptor []
    (jade/render "noncryptor.jade"))

(defn signin [req]
    (jade/render "signin.jade"))

(defn signup [req]
    (jade/render "signup.jade"))