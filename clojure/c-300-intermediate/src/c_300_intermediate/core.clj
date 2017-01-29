(ns c-300-intermediate.core
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :refer [join]]))

(def rule-no)
(def size)

(defn circular-partition [l]
  (->> (flatten [(last l) l (first l)])
       (partition 3 1)))

(defn get-rule []
  (->> rule-no
       (cl-format nil "~8,'0',B")
       (map {\0 0 \1 1})))

(defn display [tape]
  (->> tape
       (map (fn [b] ({0 " " 1 "*"} b)))
       (join nil)
       (println)))

(defn apply-rule [prev-current-next]
  (let [rule-index (apply + (map * prev-current-next '(4 2 1)))]
    (nth (reverse (get-rule)) rule-index)))

(defn tick [tape]
  (display tape)
  (recur (map apply-rule (circular-partition tape))))

(defn -main [size rows rule-no]
  (let [size (read-string size)
        rows (read-string rows)
        rule-no (read-string rule-no)
        tape (-> (apply vector (repeat size 0))
                 (assoc (quot size 2) 1))]
    (def rule-no rule-no)
    (def size size)
    (tick tape)))
