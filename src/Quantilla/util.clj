(ns quantilla.util
  "Library with a number of utility functions"
  (:require [noir.io :as io]
            [markdown.core :as md]
            [postal.core :refer [send-message]])
  (:use [clj-pdf.core]
        [ring.util.response :only [response file-response]]))

;; PDF Generating Utilities
(pdf in out)

;; Email Generating Utilities
(def email "smif@stevens.edu")
(def pass "Password")

(def conn {:host "smtp.gmail.com"
           :ssl true
           :user email
           :pass pass})

(defn send-email
  "Sends email upon lost credentials."
  [addr details]
  (send-message conn {:from email
                      :to addr
                      :subject "Login Info from SMIF"
                      :body 
                      (str "Hello,\n Your requested information is here:\n" {:user-id details} {:password details}
                        "\n Thank you.\n\nRegards,\nStevens SMIF Team")}))

;; Converts Markdownt to HTML-- Used to generate Wiki's later.
(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (md/md-to-html-string (io/slurp-resource filename)))
