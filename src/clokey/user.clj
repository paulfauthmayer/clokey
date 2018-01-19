(ns clokey.user
 (:require [crypto.password.bcrypt :as password]
           [clojure.string :as string]
           [clojure.java.io :as io]
           [cheshire.core :refer :all]))



; <editor-fold> --------FILESYSTEM READ/WRITE -----------

(defn get-path
  "path to the userdata folder on the filesystem, configuration value"
  [user]
  (str "./data/" user ".txt"))

(defn exists?
  "Checks if a file already exists"
  [user]
  (true?
   (.exists (io/file (get-path user)))))

(defn create-user-file
  "Creates a file with a given username, e.g. paul.txt"
  [name]
  (with-open [wrtr (io/writer (get-path name))]))

(defn read-file
  "Read data from a file and return as string, parses from json"
  [user]
  (if
    (exists? user)
    (parse-string (slurp (get-path user)) true)
    (println "File does not exist!")))

(defn write-to-file
  "Write a given input to a file, will create a file if none exists, parses to json"
  [user, input]
  (if (exists? user)
    (spit (get-path user) (generate-string input))
    (do
     (println (str "File does not exist, creating file " user ".txt"))
     (create-user-file user)
     (spit (get-path user) (generate-string input)))))

; </editor-fold>

; <editor-fold> ---------ENCRYPTION---------------------

(defn encrypt [pw]
  pw)

(defn decrypt [pw]
  pw)

; </editor-fold>

; <editor-fold> --------PASSWORD GENERATION -----------

(def all-chars-range (range 33 127))

(def selected-range  (concat (range 33 45)
                             (range 46 127)
                             (range 65 91) ; a-z and A-Z are included twice in order
                             (range 97 123)))

(def easy-range      (concat (range 48 58)
                             (range 97 123)
                             (range 65 91)
                             (range 33 39)
                             '(42 43 63 64)))

(defn get-valid-characters [& ranges]
  (map char (apply concat ranges)))

(defn generate-password
  "Generates a password in the pattern of XXX-XXX-XXX-XXX"
  ([] (generate-password 4 easy-range))
  ([number-of-blocks range]
   (loop [blocks []]
     (if (>= (count blocks) number-of-blocks)
       (clojure.string/join "-" blocks)
       (recur
        (into blocks
              (vector (apply str
                        (take 3 (repeatedly #(rand-nth (get-valid-characters range))))))))))))

(defn valid? [pw]
  "Checks whether a password is sufficiently secure. The password must include:
   - minimum length of 10 characters
   - at least one lower case letter
   - at least one upper case letter
   - at least one digit
   - at least one special character: - @ # $ % ^ & + ="
  (let [pattern
        #"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@#$%^&+=_])(?=\S+$).{10,30}$"]
    (and
     (string? pw)
     (re-matches pattern pw)
     true))) ; and returns the last value it evaluates if all are true

;;;; QUESTION: is it better to do (and bool bool true) or (boolean (and bool bool))

;</editor-fold>

; <editor-fold> --------USER CRUD -----------

;; CRUD - USERS

(defn save-user
  "Writes a user 'object' to the FS"
  [user]
  (write-to-file (user :name) user))

(defn create-user [username mpw]
  (let [new-user
        {:name username
         :mpw mpw
         :entries
         []}]
    (save-user new-user)
    new-user))

(defn get-user
  "Reads a user from FS by username and provides data"
  [user]
  (read-file (user :name)))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))



; </editor-fold>

; <editor-fold> -------- AUTHENTICATION -----------

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))

; </editor-fold>

; <editor-fold> --------ENTRY CRUD -----------

; //TODO: Verfiy that apply generate-password '() is the right way....
(defn create-entry
  "creates entry if requirements are met, does other stuff otherwise" ;TODO: REDACT!
  ([source username pw]
   (if (valid? pw)
    (do
      (println "success!")
      {:source source
       :username (encrypt username)
       :password (encrypt pw)})
    (println "fail!")))
  ([source username]
   {:source source
    :username (encrypt username)
    :password (apply generate-password '())}))


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


;;

;TEST

(def x (create-user "A" "ASDBJASDVAsds123#"))

(def y (set-entry x (create-entry "A.com" "AAA")))



;; TEST User

(def test-user {
                 :name "gunther"
                 :mpw "password123"
                 :entries [
                           {:source "facebook.com"
                            :username "gunterh69"
                            :password "password123"}
                           {:source "youtube.com"
                            :username "gunterh69"
                            :password "password123"}
                           {:source "banana-republic.com"
                            :username "gunterh69"
                            :password "password123"}]})
