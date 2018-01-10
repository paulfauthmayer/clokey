(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

;; CRUD - USERS

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
  (read-file user))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

(defn save-user
  "Writes a user 'object' to the FS"
  [user]
  (write-to-file (user :name) user))

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))

;; CRUD

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
    :password (encrypt generate-password)}))

(defn read-entry []
  (* 1 1))

(defn update-entry []
  (* 1 1))

(defn delete-entry []
  (* 1 1))

; <editor-fold> --------FILESYSTEM METHODS -----------

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
    (json/read-str (slurp (get-path user)))
    (println "File does not exist!")))

(defn write-to-file
  "Write a given input to a file, will create a file if none exists, parses to json"
  [user, input]
  (if (exists? user)
    (spit (get-path user) (json/write-str input))
    (do
     (println (str "File does not exist, creating file " user ".txt"))
     (create-user-file user)
     (spit (get-path user) (json/write-str input)))))

; </editor-fold>

;; MANAGE ENTRIES

(defn get-entry [user source-name]
  (let [entries (user :entries)]
    (filter
     #(re-matches (re-pattern source-name) (:source %))
     entries)))

(defn set-entry [user entry]
  (let [new-user
        {:name (:name user)
         :mpw (:mpw user)
         :entries (conj (:entries user) entry)}]
    (println "User before: \n" user)
    (conj (:entries user) entry)
    ;(save-user new-user)
    (println "User after: \n" new-user)
    new-user))


;; GENERATE PASSWORD

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

(defn generate-basic-password
  ([] (generate-password 10))
  ([length]
   (apply str (take length (repeatedly #(rand-nth valid-chars))))))

(defn generate-fancy-password
  "Generates a password in the pattern of XXX-XXX-XXX-XXX"
  ([] (generate-fancy-password 4 easy-range))
  ([number-of-blocks range]
   (loop [blocks []]
     (if (>= (count blocks) number-of-blocks)
       (clojure.string/join "-" blocks)
       (recur
        (into blocks
              (vector (apply str
                        (take 3 (repeatedly #(rand-nth (get-valid-characters range))))))))))))

;; VALIDATION

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

;; ENCRYPTION

(defn encrypt [pw]
  pw)

(defn decrypt [pw]
  pw)

;;
