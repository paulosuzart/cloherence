(ns cloherence.test.core
  (:use [cloherence core maps] :reload)
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

(deftest map-semantics
	(cache-map dogs)
	(assoc dogs 1 {:name "nina" :breed "Bulldog"}) 
        (is "nina" (:name (get dogs 1)))
	(update dogs 1 
		(fn [e] {:name (.toUpperCase (:name e))
			 :breed (:breed e)}))
        (is "NINA" (:name (get dogs 1)))
        (inplace-update dogs 1 
		(fn [e arg] {:name (.toLowerCase (:name e))
			 :breed (:breed arg)}) "bull")
	(is "nina" (:name (get dogs 1)))
	(is "bull" (:breed (get dogs 1)))) 
