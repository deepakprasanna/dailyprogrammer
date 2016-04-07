(ns c-260-easy.core
  (:require [clojure.string :as cstr]))

(def garage-door-states
  {:closed {:button_clicked :opening}
   :open {:button_clicked :closing}
   :opening {:button_clicked :stopped_while_opening, :cycle_complete :open}
   :closing {:button_clicked :stopped_while_closing, :cycle_complete :closed}
   :stopped_while_opening {:button_clicked :closing}
   :stopped_while_closing {:button_clicked :opening}})


(def challenge-seq
  [:button_clicked
   :cycle_complete
   :button_clicked
   :button_clicked
   :button_clicked
   :button_clicked
   :button_clicked
   :cycle_complete])

(defn process-commands [commands-seq]
  (loop [commands commands-seq
         states [:closed]]
    (if (not (empty? commands))
      (let [current-state (last states)
            command (first commands)
            new-state ((garage-door-states current-state) command)]
        (recur (drop 1 commands) (conj states new-state)))
      states)))

(defn -main []
  (doall
    (let [states (process-commands challenge-seq)
          commands (conj challenge-seq "")]
      (map (fn [command state]
             (prn (str "Door: " (cstr/upper-case (name state))))
             (prn (cstr/capitalize (name command)))) commands states))))
