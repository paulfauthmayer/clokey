(ns clokey.pass
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]))

;; CRUD

(defn create-entry
  "creates entry"
  ([source username pw]
   {:source source
    :username (encrypt username)
    :password (encrypt pw)})
  ([source username]
   {:source source
    :username (encrypt username)
    :password (encrypt generate-password)}))

(defn read-entry []
  (* 1 1))

(defn update-entry []
  (* 1 1))

(defn delete-entry []
  (* 1 1))

;; VALIDATION

(defn valid? []
  (* 1 1))

;; ENCRYPTION

(defn encrypt [pw]
  pw)

(defn decrypt []
  (* 1 1))

;; GENERATE PASSWORD

(defn generate-password
  []
  ("HARDCODED-PASSWORD"))
