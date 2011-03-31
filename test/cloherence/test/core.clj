(ns cloherence.test.core
  (:use [cloherence.core] :reload)
  (:use [clojure.test]))

(def anim1 {:name "Nacho" :breed "Bulldog"})
(def anim2 {:name "Taco" :breed "Bulldog"})
(def animals (list anim1 anim2))

(deftest put-in-cache
  (with-cache "animals" 
  	(is 1 (put-val 1 anim1))
	(is anim1 (get-val 1))
	(is nil? (put-seq :name animals))
	(is "Nacho" (:name (get-val "Nacho")))))
	;(is "Taco" (:name (remove "Taco")))))
