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

(defn valid? [pw]
  "Checks whether a password is sufficiently secure. The password must include:
   - minimum length of 10 characters
   - at least one lower case letter
   - at least one upper case letter
   - at least one digit
   - at least one special character: - @ # $ % ^ & + ="
  (let [pattern
        #"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@#$%^&+=_])(?=\S+$).{10,}$"]
    (boolean (re-matches pattern pw))))

;; ENCRYPTION

(defn encrypt [pw]
  pw)

(defn decrypt []
  (* 1 1))

;; GENERATE PASSWORD

(defn generate-password
  ([] (generate-password 10))
  ([length]
   (let [valid-chars (map char (range 33 127))]
     (take length (repeatedly (rand-nth valid-chars))))))
