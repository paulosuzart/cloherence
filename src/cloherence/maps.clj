; Copyright (c) Paulo Suzart. All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.html at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns ^{:doc "core clojerence abstraction with clojure map semantics."
      :author "Paulo Suzart"}
     	cloherence.maps
	(:use [clojure.contrib.def :only [name-with-attributes]]
              [cloherence.core]))


	(defmacro cache-map
		"Simpli binds name in the namespace as a string" 
		[name & kvs]
    	(let [[name kvs] (name-with-attributes name kvs)]
			`(do
      			(def ~name ~(str name)))))
	
	(defn assoc
		([cache e-key e-val]
			(with-cache cache	
				(put-val e-key e-val)))
		([cache e-key e-val & kvs]
				(.putAll (get-cache cache) 
					(loop [result {} coll (cons e-key (cons e-val kvs))]
						(if (empty? coll)
							result
							(recur 
								(merge result {(first coll) (second coll)}) (nnext coll)))))))
	(defn count
		"Returns the current count of entries"
		[cache]
		(.size (get-cache cache)))
	
	(defn get
		"Returns nil if not-found is not suplied for not found entries.
		 Returns value for e-key."
		([cache e-key]
			(with-cache cache (get-val e-key)))
		([cache e-key not-found]
			(or (get cache e-key) not-found)))

	(defn contains?
		"Check if a given key is present. Returns true if present."
		[cache e-key]
		(with-cache cache (.containsKey *cache* e-key)))
	
	(defn update
		"Like clojure original update-in, but doesn't allow chainned keys.
		 Works applying f to respective e-key value"
		([cache e-key f & args]
			(assoc cache e-key (apply f (get cache e-key) args))))
