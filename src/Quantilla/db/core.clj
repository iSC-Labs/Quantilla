(ns quantilla.db.core
  "Database access and update functions"
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [quantilla.db.schema :as schema]
            [taoensso.carmine :as car :refer [wcar]]))

;; First, set up the Redis session store.
(def redis-conn { :pool {} :spec { :host "127.0.0.1" :port "6379" :password "secret" :timeout-ms (* 30 1000) :db 3}})

;; Just for clarity of code.
(defmacro wcar* [& body]
  `(car/wcar redis-conn ~@body))

;;============================= User Profile Database =============================

;; Insert new user/user registration
;; Users database is a hash.
(defn create-user [{id :id :as userData}]
  (wcar* (car/hmset* (str "users:" id) userData)))

;; Update user profile
(defn update-user [id name email password]
  (wcar* (car/hmset (str "users:" id) [:name name :email email :pass password] )))

;; Reset password
(defn update-password [id newPass]
  (wcar* (car/hset (str "users:" id) :pass newPass)))

;; Check user exists.
(defn exists-user [id]
  (wcar* (car/exists (str "users:" id))))

;; Return whole user data map.
(defn get-user [id]
  (wcar* (car/hgetall (str "users:" id))))

;; Delete user profile
(defn delete-user [id]
  (wcar* (car/del (str "users:" id))))

;;============================ Portfolios Database ===============================


;;============================ Historical Time Series Database =======================


;;============================ Analytics Database ==================================


;; Sets functions for MySQL table of user profiles

(defdb db schema/db-spec)  ;; Define db based on schema map.

;; Define connection to particular
(defentity auth-users
  (table :users))

;; User registration
(defn create-user [user]
  (insert auth-users
          (values user)))

;; User updates
(defn update-user [id first-name last-name email]
  (update auth-users
  (set-fields {:first_name first-name
               :last_name last-name
               :email email})
  (where {:id id})))

;; Reset Password.
(defn update-password [id password]
  (update auth-users
    (set-fields {:password password})
    (where {:id id})))

;; Retrieve user profile info.
(defn get-user [id]
  (first (select auth-users
                 (where {:id id})
                 (limit 1))))
