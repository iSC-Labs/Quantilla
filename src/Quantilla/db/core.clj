(ns quantilla.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [quantilla.db.schema :as schema]))

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
