(ns custom.database
    (:require [clojure.java.jdbc :as sql]))
          
(def db-config {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname (str "//localhost:3306/bugreport?characterEncoding=utf-8")
         :user "root"
         :password ""})

(defn check-conn
  "Checks MySQL connection"
  []
  (try
         (do (sql/query db-config ["SELECT version();"])
             true)
  (catch Exception e 
         (do
             (println (str "caught exception: " (.getMessage e)))
             false))))