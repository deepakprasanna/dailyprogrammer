(ns c-300-easy.core)

(defn -main []
  (let [player (org.jfugue.Player.)]
    (.play player "C D E F G A B")))
