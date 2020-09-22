(ns simple-pendulum
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def g 9.81)

(defn create-points
  [{:keys [l
           theta0
           t0 tf dt]}]
  (let [omega (Math/sqrt (/ g l))
        period (/ (* 2 Math/PI) omega)]
    (for [t (range t0 tf dt)]
      (let [theta (* theta0 omega (Math/cos (* omega (- t t0))))
            dtheta-dt (* -1 omega theta0 (Math/sin (* omega (- t t0))))
            x (* l (Math/sin theta))
            y (* -1 l (Math/cos theta))
            vx (* l dtheta-dt (Math/cos theta))
            vy (* l dtheta-dt (Math/sin theta))]
        {:t t :x x :y y :vx vx :vy vy :theta theta :dtheta-dt dtheta-dt}))))

(defn animate-trajectory [path-data x-val y-val]
  (doall
   (for [datum path-data]
     (do
       (oz/view! {:data {:values [datum]}
                  :encoding {:x {:field x-val :type "quantitative" :scale
                                 {:domain [-1 1]}}
                             :y {:field y-val :type "quantitative"
                                 :scale {:domain [-1 1]}}}
                  :mark "point"})
       (Thread/sleep 100)))))

(defn plot-path [path-data x-val y-val]
  (oz/view! {:data {:values path-data}
             :encoding {:x {:field x-val :type "quantitative"}
                        :y {:field y-val :type "quantitative"}
                        :order {:field "t"}}
             :mark "line"}))

(def initial-values {:l 1
                     :theta0 0.314
                     :t0 0
                     :tf 20
                     :dt 0.01})

(defn plot-all []
  (oz/view!
   {:data {:values (create-points initial-values)}
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
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "theta" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "dtheta-dt" :type "quantitative"}
                 :order {:field "t"}}}
     ]}))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points initial-values "x" "y"))))

