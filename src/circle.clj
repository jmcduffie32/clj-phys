(ns circle
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def omega 1)
(def x0 0)
(def y0 0)
(def R 1)
(def t0 0)

(defn create-points []
  (for [t (range 0 30 0.01)]
    (let [theta (* omega (- t t0))
          x (+ x0 (* R (Math/cos theta)))
          y (+ y0 (* R (Math/sin theta)))
          vx (* -1 omega R (Math/sin theta))
          vy (* omega R (Math/cos theta))]
      {:t t :x x :y y})))

(defn animate-trajectory [path-data]
  (doall
   (for [{:keys [t x y]}  path-data]
     (do
       (oz/view! {:data {:values [{:t t :x x :y y}]}
                  :encoding {:x {:field "x" :type "quantitative" :scale
                                 {:domain [-1 1]}}
                             :y {:field "y" :type "quantitative"
                                 :scale {:domain [-1 1]}}
                             :order {:field "t"}}
                  :mark "point"})
       (Thread/sleep 100)))))

(defn plot-path [path-data]
  (oz/view! {:data {:values path-data}
             :encoding {:x {:field "x" :type "quantitative" :scale
                            {:domain [-1 1]}}
                        :y {:field "y" :type "quantitative"
                            :scale {:domain [-1 1]}}
                        :order {:field "t"}}
             :mark "line"}))

(defn -main []
  (do
    (oz/start-server!)
    (plot-path (create-points))))

