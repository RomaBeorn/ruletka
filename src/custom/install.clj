(ns custom.install
  (:require [clojure.java.jdbc :as sql]
            [compojure.core :refer :all]
            [custom.database :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect]])
    (:use clojure.java.io))


(defn install []
   "Cryptor table structure"
  (sql/execute! db/db-config ["drop table if exists report"])
  (sql/execute! db/db-config ["drop table if exists users"])
  (sql/execute! db/db-config ["drop table if exists counter"])

   
   (sql/db-do-commands db/db-config
    (sql/create-table-ddl :users
                          [:id_user "integer" "PRIMARY KEY" "AUTO_INCREMENT"]
                          [:user_name "varchar(255)"]
                          [:user_role "integer"]
                          [:md5 "text"])
                        
    (sql/create-table-ddl :report
                          [:id "integer" "PRIMARY KEY" "AUTO_INCREMENT"]
                          [:name "text"]
                          [:text "text"]
                          [:email "text"])
    (sql/create-table-ddl :counter
                          [:id "integer" "PRIMARY KEY" "AUTO_INCREMENT"]))
)
    ; (sql/execute! db/db-config ["ALTER TABLE users CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;"])