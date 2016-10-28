(ns custom.core
    (:require
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.defaults :refer :all]
    [compojure.core :refer :all]
    [compojure.handler :as handler]
    [compojure.route :as route]
    [custom.layout :as layout]
    [custom.database :refer :all]
    [clojure.java.jdbc :as sql]
    [custom.install :refer :all]
    [ring.util.response :refer :all]
    [ring.middleware.session :refer :all]
    [ring.middleware.session.memory :refer :all]
    [hiccup.core :refer :all]
    [hiccup.form :refer :all])
  (:gen-class))

(import 'java.security.MessageDigest)
(import 'java.math.BigInteger)

(def abc {:nam "1"})


(defn parse-int [s]
  (Integer. (re-find  #"\d+" s )))

(defn insert-report [a b c]
    "signup user into database"
    (sql/insert! db-config :report {:name a :text b :email c}))
  
(defn delete-report [id]
    "delete post from 'posts' table"
    (sql/delete! db-config :report ["id = ?" id]))
    
(defn get-bugs []
  (sql/query db-config ["SELECT * FROM report"]))  
  
  (defn md5 [s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        size (* 2 (.getDigestLength algorithm))
        raw (.digest algorithm (.getBytes s))
        sig (.toString (BigInteger. 1 raw) 16)
        padding (apply str (repeat (- size (count sig)) "0"))]
    (str padding sig)))
                
(defn check-username [a] 
    "check username"
    (sql/query db-config ["select user_name from users where user_name=?" a]))

(defn check-password [a]
    "check password"
    (sql/query db-config ["select md5 from users where user_name=?" a]))

(defn insert-user [a b]
    "signup user into database"
    (sql/insert! db-config :users {:user_name a :md5 (md5 (str b)) :user_role "999"}))
                

(defn check-session [req] 
    (println (:name (:session req)))
    (= (str (:name (:session req)))))

(defroutes athene-routes
    (GET "/signup" [req]
             (layout/signup req))
         
    (POST "/signup" [signup_login signup_password :as req]
          (println signup_login signup_password)
            (let [usernamesign {:user_name (str signup_login)}] 
                (if (= (str (list (check-username (str signup_login)))) (str "((" usernamesign "))"))
                "Пользователь с таким никнеймом уже существует"
                (and (insert-user (str signup_login) signup_password) (redirect "/signin")))))
            
    (GET "/signin" [req]
            (layout/signin req))
        
    (POST "/signin" [signin_login signin_password :as req]
            (println signin_login signin_password)
            (let [username {:user_name (str signin_login)} signin_password_with_md5 {:md5 (str (md5 (str signin_password)))}]       
                (if (= (str (list (check-username (str signin_login)))) (str "((" username "))"))
                    (if (= (str (list (check-password (str signin_login)))) (str "((" signin_password_with_md5 "))")) 
                        (assoc req :session {:name signin_login :password signin_password})
                        "Неправильный пароль")
                        "Неправильный логин или пароль")))
                    
    (GET "/profile" [:as req]
            (println (str (:password (:session req))))
            (html [:h1 (str "Имя: " (:name (:session req)))]
                  [:h1 (str "Это ваш пароль для доступа к учетной записи, запишите его на листок, чтобы не потерять - ") (str (:password (:session req)))]))
    
    (GET "/" [:as req]
        (redirect "/home"))
    
    (GET "/home" [:as req]
        (layout/home))
    
    (GET "/mysql-enable" [:as req] 
        (layout/db-not-conn))
    
    (GET "/report" [req]
        (layout/report))
    
    (POST "/report" [name text email :as req]
          (insert-report name text email)
          (redirect "/"))
    
    (GET "/list-of-reports" []
         (layout/list-of-reports (get-bugs)))
    
    (GET "/report/:id" [id]
         (delete-report id)
         (redirect "/list-of-reports"))
    
    (GET "/install" []
         (install))
    
    (GET "/contacts" []
         (layout/contacts))
    
    ; (GET "/list-of-users" [req2]
    ;      (layout/users))
    
    (route/resources "/"))

(defn check-session [req]
    (if (= (str (:name (:session req))) "")
        (redirect "/home")))



(defn wrap-checks
  "Checking MySQL Connection & Installed Flag Middleware"
  [handler]
  (fn [req]
      (let [mysql-req (assoc req :uri "/mysql-enable")]
                (println (req :uri))
                (println (:name (:session req)))
                (if (check-conn) 
                    (if (check-session req)
                        (if (or 
                                (= (str (req :uri)) "/signin")
                                (= (str (req :uri)) "/signup")
                                (= (str (req :uri)) "/home")
                                (= (str (req :uri)) "/profile")
                                )
                            (handler req)
                            (redirect "/home"))
                        (handler req))
                    (handler mysql-req)))))

(def app (wrap-checks (wrap-session (wrap-params athene-routes) {:store (memory-store)} )))
          










; (defn wrap-checks
;   "Checking MySQL Connection & Installed Flag Middleware"
;   [handler]
;   (fn [req]
;       (let [mysql-req (assoc req :uri "/mysql-enable")]
;                 (println (req :uri))
;                 (if (check-conn) 
;                     (handler req)
;                     (handler mysql-req)))))





; (def app (->(routes athene-routes)
;             (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
;             (wrap-params)
;             (wrap-session {:store (memory-store) :cookie-attrs {:max-age 360000}})
;             (wrap-checks)))