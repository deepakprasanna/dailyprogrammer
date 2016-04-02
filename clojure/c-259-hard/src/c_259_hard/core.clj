(ns c-259-hard.core)

(def nos-operators {\0 +, \1 -, \2 *})

;; lazy nos-sequence upto depth of 10
(def nos-numbers
  (loop [i 1, nos '("0", "1", "2"), acc '("0" "1" "2")]
    (if(> i 10)
      acc
      (let [newlist (for [j nos, k (range 3)] (str j k))]
        (recur (inc i) newlist (concat acc newlist))))))

(defn decode-operators [nos-number]
  (map (partial get nos-operators) nos-number))

(defn nos-to-int [nos-number]
  (last (let [sum (atom 0), operators (decode-operators nos-number)]
     (map (fn [o n] (swap! sum o n)) operators (iterate inc 1)))))

(defn all-possible-nos-for-number [number]
  (get (group-by nos-to-int nos-numbers) (Integer/parseInt number)))

(defn get-nos-with-shortest-length [nos-numbers]
  (let [nos-number-grouped-by-length (group-by count nos-numbers)
        shortest-length (first (sort (keys nos-number-grouped-by-length)))]
    (get nos-number-grouped-by-length shortest-length)))

(defn int-to-nos [number]
  (->> number (all-possible-nos-for-number)
       (get-nos-with-shortest-length)))
