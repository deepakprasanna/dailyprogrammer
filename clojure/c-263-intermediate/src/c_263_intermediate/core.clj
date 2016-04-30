(ns c-263-intermediate.core
  (:require [clojure.set :as cset]
            [clojure.string :as cstr]))

(defn update-values [m f & args]
   (reduce (fn [r [k v]] (assoc r k (apply f v args))) {} m))

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

(def inverted-dictionary-without-digits
  (cset/map-invert dictionary-without-digits))

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

(defn find-rhyming-phonemes [word]
  (let [phoneme (dictionary-without-digits word)
        slice (slice-from-last-vowel phoneme)
        matches (group-by-last-vowel-slice slice)]
    (map #(inverted-dictionary-without-digits %) matches)))

(defn find-match-count [p1 p2]
  (loop [p1-seq (reverse (cstr/split p1 #" "))
         p2-seq (reverse (cstr/split p2 #" "))
         length 0]
    (if (and (not (empty? p1-seq)) (not (empty? p2-seq)) (= (first p1-seq) (first p2-seq)))
      (recur (rest p1-seq) (rest p2-seq) (inc length))
      length)))

(defn -main [word]
  (doall ;; ugly hack to make the lazy map print
    (let [word-phone-without-digits (dictionary-without-digits word)
          matches (find-rhyming-phonemes word)
          matches-phones (map #(dictionary %) matches)
          matches-phones-without-digits (map #(dictionary-without-digits %) matches)
          matches-count (map #(find-match-count word-phone-without-digits %) matches-phones-without-digits)]
      (map #(println %1 %2 %3) matches-count matches matches-phones))))
