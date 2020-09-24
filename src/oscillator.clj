(ns oscillator
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def max-t 5)

(defn create-points
  [{:keys [omega1 omega2 t0 tf dt]}]
  (let [period1 (/ (* 2 (Math/PI)) omega1)
        period2 (/ (* 2 (Math/PI)) omega2)]
    (for [t (range t0 tf dt)]
      (let [x (Math/cos (* omega1 t))
            y (Math/sin (* omega2 t))
            vx (* -1 omega1 (Math/sin (* omega1 t)))
            vy (* -1 omega2 (Math/cos (* omega2 t)))]
        {:t t :x x :y y :vx vx :vy vy}))))

(defn animate-trajectory [path-data]
  (doall
   (for [datum path-data]
     (do
       (oz/view! {:data {:values [datum]}
                  :encoding {:x {:field "x" :type "quantitative"
                                 :scale {:domain [0 20]}}
                             :y {:field "y" :type "quantitative"
                                 :scale {:domain [0 20]}}}
                  :mark "point"})
       (Thread/sleep 100)))))

(defn plot-path [path-data]
  (oz/view! {:data {:values (take-while #(< (:t %) (:tf initial-values))
                                        path-data)}
             :encoding {:x {:field "x" :type "quantitative"}
                        :y {:field "y" :type "quantitative"}
                        :order {:field "t"}}
             :mark "line"}))

(def initial-values {:omega1 3
                     :omega2 5
                     :t0 0
                     :tf 20
                     :dt 0.01})

(defn plot-all []
  (oz/view!
   {:data {:values (take-while #(< (:t %) (:tf initial-values))
                               (create-points initial-values))}
    :columns 2
    :concat
    [
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "x" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "y" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "vx" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "vy" :type "quantitative"}
                 :order {:field "t"}}}
     ]}))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points initial-values "x" "y"))))

