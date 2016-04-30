(ns c-264-hard.core
  (:require [clojure.set :as cset]
            [clojure.string :as cstr]))

(def char-seq
  (->> (range (int \A) (inc (int \Z))) (map char)))

(defn update-values [m f & args]
   (reduce (fn [r [k v]] (assoc r k (apply f v args))) {} m))

(def example-poem
  "A bather whose clothing was strewed
  By winds that left her quite nude
  Saw a man come along
  And unless we are wrong
  You expected this line to be lewd.")

(def challenge-poem1
  "There once was a young lady named bright
  Whose speed was much faster than light
  She set out one day
  In a relative way
  And returned on the previous night.")

(def challenge-poem2
  "Once upon a midnight dreary, while I pondered, weak and weary,
  Over many a quaint and curious volume of forgotten lore—
  While I nodded, nearly napping, suddenly there came a tapping,
  As of some one gently rapping, rapping at my chamber door.
  \"'Tis some visiter,\" I muttered, \"tapping at my chamber door—
              Only this and nothing more.\"")

(def challenge-poem3
  "Brothers, who when the sirens roar
  From office, shop and factory pour
  'Neath evening sky;
  By cops directed to the fug
  Of talkie-houses for a drug,
  Or down canals to find a hug")

(def challenge-poem4
  "Two roads diverged in a yellow wood,
  And sorry I could not travel both
  And be one traveler, long I stood
  And looked down one as far as I could
  To where it bent in the undergrowth;")

(def dictionary (->> (clojure.java.io/resource "cmudict-0.7b")
                       (slurp)
                       (cstr/split-lines)
                       (filter #(not= (take 3 %) [\; \; \;]))
                       (map #(cstr/split % #"  "))
                       (into {})))

(def vowels (->> (clojure.java.io/resource "cmudict-0.7b.phones")
                 (slurp)
                 (cstr/split-lines)
                 (map #(cstr/split % #"\t"))
                 (filter #(= (last %) "vowel"))
                 (into {})
                 (keys)))

(def dictionary-without-digits
  (update-values dictionary cstr/replace #"\d" ""))

(defn slice-from-last-vowel [phoneme]
  (let [indexes (map #(.lastIndexOf phoneme %) vowels)
        last-index (apply max indexes)]
    (->> phoneme
         (drop last-index)
         (apply str))))

(def group-by-last-vowel-slice
  (->> dictionary-without-digits
       (vals)
       (group-by slice-from-last-vowel)))

(defn get-leaf-words-for-poem [poem]
  (->> (cstr/split-lines poem)
       (map cstr/trim)
       (map #(cstr/replace % #"[^A-Za-z\s]" ""))
       (map #(cstr/split % #" "))
       (map last)
       (map cstr/upper-case)))

(defn get-leaf-phoneme-for-poem [poem]
  (->> poem
       (get-leaf-words-for-poem)
       (map #(dictionary-without-digits %))))

(defn get-rhyme-sequence [poem]
  (let [sliced-leaf-phones (->> (get-leaf-phoneme-for-poem poem)
                                (map slice-from-last-vowel))
        base-leaf-seq      (->> (distinct sliced-leaf-phones)
                                (map (fn [c phone] [phone c]) char-seq)
                                (into {}))]
    (->> (map #(base-leaf-seq %) sliced-leaf-phones)
         (apply str))))
