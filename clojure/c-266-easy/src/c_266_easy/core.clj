(ns c-266-easy.core
  (:require [clojure.string :refer :all]))

(defn bool->int [b]
  (if b 1 0))

(def input (->> "resources/nodes.txt" slurp split-lines))

(def no-of-nodes (->> input first read-string))

(def edges (->> input
                rest
                (map #(split % #" "))
                (map #(map read-string %))))

(def edges-set (set edges))

(def degrees (->> edges flatten frequencies))

(defn connected? [n1 n2]
  (or (contains? edges-set [n1 n2])
      (contains? edges-set [n2 n1])))

(defn print-edges []
  (dotimes [n no-of-nodes]
    (prn (str "Node " (inc n) " has a degree of " (degrees (inc n))))))

(defn adj-row [n]
  (->> (range 1 (inc no-of-nodes))
       (map #(connected? n %))
       (map bool->int)))

(defn print-adj-matrix []
  (dotimes [n no-of-nodes]
    (prn (adj-row n))))

(defn -main []
  (prn "### Degrees ###")
  (print-edges)
  (prn)
  (prn "### Adjacency Matrix ###")
  (print-adj-matrix))
