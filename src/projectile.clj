(ns projectile
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def g 9.81)
(def max-t 5)

(defn create-points
  [{:keys [theta v0 k t0 tf dt]}]
  (let [theta (/ (* Math/PI theta) 180)
        v0x (* v0 (Math/cos theta))
        v0y (* v0 (Math/sin theta))]
    (for [t (range t0 tf dt)]
      (let [x (* (/ v0x k) (- 1 (Math/exp (* -1 k t))))
            y (- (* (/ 1 k)
                    (+ v0y (/ g k))
                    (- 1 (Math/exp (* -1 k t))))
                 (* (/ g k)
                    t))
            vx (* v0x (Math/exp (* -1 k t)))
            vy (- (* (+ v0y (/ g k)) (Math/exp (* -1 k t))) (/ g k))]
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
  (oz/view! {:data {:values path-data}
             :encoding {:x {:field "x" :type "quantitative"
                            :scale {:domain [0 20]}}
                        :y {:field "y" :type "quantitative"
                            :scale {:domain [0 20]}}
                        :order {:field "t"}}
             :mark "line"}))

(def initial-values {:v0 10
                     :theta 45
                     :k 1
                     :t0 0
                     :tf 20
                     :dt 0.01})

(defn plot-all []
  (oz/view!
   {:data {:values (take-while #(< (:t %) max-t)
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

