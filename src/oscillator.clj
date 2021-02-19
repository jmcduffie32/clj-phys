(ns oscillator
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def omega1 5)
(def omega2 5)
(def t0 0)
(def tf 50)
(def dt 0.01)

(defn x [t omega]
  (Math/cos (* omega t)))

(defn y [t omega]
  (Math/sin (* omega t)))

(defn vx [t omega]
  (* -1 omega (Math/sin (* omega t))))

(defn vy [t omega]
  (* -1 omega (Math/cos (* omega t))))

(defn create-points
  [omega1 omega2]
  (map
   (fn [t] {:t t
            :x (x t omega1)
            :y (y t omega2)
            :vx (vx t omega1)
            :vy (vy t omega2)})
   (range t0 tf dt)))

(defn animate-trajectory [path-data]
  (doseq [datum path-data]
    (do
      (oz/view! {:data {:values [datum]}
                 :encoding {:x {:field "x" :type "quantitative"
                                :scale {:domain [-1 1]}}
                            :y {:field "y" :type "quantitative"
                                :scale {:domain [-1 1]}}}
                 :mark "point"})
      (Thread/sleep 100))))

(defn plot-path [path-data]
  (oz/view! {:data {:values (take-while #(< (:t %) tf)
                                        path-data)}
             :encoding {:x {:field "x" :type "quantitative"}
                        :y {:field "y" :type "quantitative"}
                        :order {:field "t"}}
             :mark "line"}))


(defn plot-all []
  (oz/view!
   {:data {:values (take-while #(< (:t %) tf)
                               (create-points omega1 omega2))}
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

(defn save-data [path-data]
  (doseq [{:keys [t x y vx vy]} path-data]
    (spit "oscillator.dat" (str/join " " [t x y vx vy "\n"]) :append true)))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points omega1 omega2))))

(comment
  (oz.server/stop!)
  (oz/start-server!)

  (plot-path (create-points omega1 omega2))

  (def omega1 3)
  (def omega2 2)
  (doseq [omega (range 10)]
    (do
      (plot-path (create-points omega omega2))
      (Thread/sleep 1000)))

  (animate-trajectory (create-points omega1 omega2))

  ,)
