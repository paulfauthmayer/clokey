(ns clokey.user
 (:require [clojure.string :as string]
           [clojure.java.io :as io]
           [cheshire.core :refer :all]
           [clokey.password :as password]
           [clokey.utils :as utils]))

; <editor-fold> --------USER CRUD -----------

;; CRUD - USERS

(defn save-user
  "Writes a user 'object' to the FS"
  [user]
  (utils/write-to-file (user :name) user))

(defn create-user [username email mpw]
  (let [new-user
        {:name username
         :mpw mpw
         :email email
         :entries
         []}]
    (save-user new-user)
    new-user))

(defn get-user
  "Reads a user from FS by username and provides data"
  [user]
  (utils/read-file (user :name)))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

; </editor-fold>

; <editor-fold> --------ENTRY CRUD -----------

; //TODO: Verfiy that apply generate-password '() is the right way....
(defn create-entry
  "creates entry if requirements are met, does other stuff otherwise" ;TODO: REDACT!
  ([source username pw]
   (if (password/valid? pw)
    (do
      (println "success!")
      {:source source
       :username (utils/encrypt username)
       :password (utils/encrypt pw)})
    (println "fail!")))
  ([source username]
   {:source source
    :username (utils/encrypt username)
    :password (utils/encrypt (apply password/generate-password '()))}))


(defn read-entry []
  (* 1 1))

(defn update-entry []
  (* 1 1))

(defn delete-entry [user source-name]
  (let [new-user {
                  :name (user :name)
                  :mpw (user :mpw)
                  :entries (remove
                            (fn [entry]
                              (= (entry :source) source-name))
                            (user :entries))}]
    (save-user new-user)))



;; MANAGE ENTRIES

(defn get-entry
  ""
  [user source-name]
  (let [entries (user :entries)]
    (filter
     #(re-matches (re-pattern source-name) (:source %))
     entries)))

(defn set-entry
  ""
  [user entry]
  (let [new-user
        {:name (:name user)
         :mpw (:mpw user)
         :entries (conj (:entries user) entry)}]
    (println "User before: \n" user)
    (conj (:entries user) entry)
    (save-user new-user)
    (println "User after: \n" new-user)
    new-user))

; </editor-fold>

; <editor-fold> -------- AUTHENTICATION -----------

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))

; </editor-fold>
