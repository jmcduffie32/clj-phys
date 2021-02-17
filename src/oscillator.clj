(ns oscillator
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def omega1 3)
(def omega2 5)
(def t0 0)
(def tf 50)
(def dt 0.01)


(comment
  (oz/start-server!)

  (plot-path (create-points))

  (def ^:dynamic omega1 0)
  (def ^:dynamic omega2 1)
  (doall (for [omega (range 0 1.01 0.01)]
           (do
             (binding [omega1 omega]
               (plot-path (create-points)))
             (Thread/sleep 500))))

  (animate-trajectory (create-points))

  ,)

(defn x [t]
  (Math/cos (* @#'omega1 t)))

(defn y [t]
  (Math/sin (* @#'omega2 t)))

(defn vx [t]
  (* -1 omega1 (Math/sin (* @#'omega1 t))))

(defn vy [t]
  (* -1 omega2 (Math/cos (* @#'omega2 t))))

(defn create-points
  []
  (println #'omega1)
  (for [t (range t0 tf dt)]
    {:t t :x (x t) :y (y t) :vx (vx t) :vy (vy t)}))

(defn animate-trajectory [path-data]
  (doall
   (for [datum path-data]
     (do
       (oz/view! {:data {:values [datum]}
                  :encoding {:x {:field "x" :type "quantitative"
                                 :scale {:domain [-1 1]}}
                             :y {:field "y" :type "quantitative"
                                 :scale {:domain [-1 1]}}}
                  :mark "point"})
       (Thread/sleep 100)))))

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
                               (create-points))}
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
      (plot-path (create-points))))

