(ns full-control.core
  (:require-macros [full-control.core :refer [defcolumn
                                              gen-om-fns
                                              deflbl-col
                                              deftxt-col
                                              deftxtarea-col
                                              defdropdown-col
                                              defcheckbox-col
                                              defhelp-col]])
  (:require [clojure.string :as str]
            [goog.string :as gstr]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [full-control.utils :as utils :refer [generate-attrs
                                                  col-size-css
                                                  column-class-names
                                                  input-class-names
                                                  validation-state-class-names
                                                  float-class-names
                                                  table-class-names
                                                  form-group-class-names]]))

;;;
;;; Page record and fns
;;;

;; Implements the om.core/IRenderState protocol. Expects m as its constructor
;; parameter. m must be a map with functions as values which returns the body
;; to be used in the protocol's functions.
(defrecord Component [m]
  om/IInitState
  (init-state [_]
    ((:init-state-fn m)))

  om/IWillMount
  (will-mount [_]
    ((:will-mount-fn m)))

  om/IRenderState
  (render-state [_ state]
    ((:render-state-fn m) state)))

(defn root [f value options]
  (om/root f value options))

(defn build
  ([f x] (om/build f x))
  ([f x m] (om/build f x m)))

(defn get-state
  ([owner] (om/get-state owner))
  ([owner korks] (om/get-state owner korks)))

(defn set-state! [owner korks v]
  (om/set-state! owner korks v))

(defn update-state!
  ([owner f] (om/update-state! owner f))
  ([owner korks f] (om/update-state! owner korks f)))

(defn transact!
  ([cursor f] (om/transact! cursor f))
  ([cursor korks f] (om/transact! cursor korks f))
  ([cursor korks f tag] (om/transact! cursor korks f tag)))

(defn update!
  ([cursor v] (om/update! cursor v))
  ([cursor korks v] (om/update! cursor korks v))
  ([cursor korks v tag] (om/update! cursor korks v tag)))

(defn cursor? [x]
  (om/cursor? x))

;;;
;;; General controls
;;;

(def nbsp* (gstr/unescapeEntities "&nbsp;"))

(defn space* [& _] nbsp*)

;; All om.dom/tags
(gen-om-fns)

(defn btn*
  "Attributes available in the attrs map are :class-name, :on-click."
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply button* (generate-attrs attrs
                                 :defaults {:type "button"
                                            :class-name "btn btn-default"})
         body))

(defn lbl* [attrs & body]
  {:pre [(map? attrs)]}
  (apply label* (generate-attrs attrs
                                :defaults {:class-name (column-class-names attrs
                                                                           "control-label")})
         body))

(defn txt* [attrs & body]
  {:pre [(map? attrs)]}
  (apply input* (generate-attrs attrs
                                :defaults {:type "text"
                                           :class-name (input-class-names attrs
                                                                          "form-control")})
         body))

(defn txtarea* [attrs & body]
  {:pre [(map? attrs)]}
  (apply textarea* (generate-attrs attrs
                                   :defaults {:class-name (input-class-names attrs
                                                                             "form-control")})
         body))

(defn dropdown* [attrs & body]
  {:pre [(map? attrs)]}
  (apply select* (generate-attrs attrs
                                 :defaults {:class-name (input-class-names attrs
                                                                           "form-control")})
         body))

(defn checkbox* [attrs & body]
  {:pre [(map? attrs)]}
  (div* (generate-attrs attrs
                        :defaults {:class-name "checkbox"
                                   :display (:display attrs)}
                        :depth [:div])
        (apply label* (generate-attrs attrs
                                      :depth [:div :label])
               (cons (input* (generate-attrs attrs
                                             :defaults {:type "checkbox"
                                                        :id (:id attrs)
                                                        :checked (:checked attrs)
                                                        :on-change (:on-change attrs)
                                                        :disabled (:disabled attrs)}
                                             :depth [:div :label :input]))
                     body))))

(defn checkbox-inline* [attrs & body]
  {:pre [(map? attrs)]}
  (apply label* (generate-attrs attrs
                                :defaults {:class-name "checkbox-inline"
                                           :display (:display attrs)}
                                :depth [:label])
         (cons (input* (generate-attrs attrs
                                       :defaults {:type "checkbox"
                                                  :id (:id attrs)
                                                  :checked (:checked attrs)
                                                  :on-change (:on-change attrs)
                                                  :disabled (:disabled attrs)}
                                       :depth [:label :input]))
               body)))

(defn radio* [attrs & body]
  {:pre [(map? attrs)]}
  (div* (generate-attrs attrs
                        :defaults {:class-name "radio"
                                   :display (:display attrs)}
                        :depth [:div])
        (apply label* (generate-attrs attrs
                                      :depth [:div :label])
               (cons (input* (generate-attrs attrs
                                             :defaults {:type "radio"
                                                        :id (:id attrs)
                                                        :name (:name attrs)
                                                        :value (:value attrs)
                                                        :checked (:checked attrs)
                                                        :on-change (:on-change attrs)
                                                        :disabled (:disabled attrs)}
                                             :depth [:div :label :input]))
                     body))))

(defn radio-inline* [attrs & body]
  {:pre [(map? attrs)]}
  (apply label* (generate-attrs attrs
                                :defaults {:class-name "radio-inline"
                                           :display (:display attrs)}
                                :depth [:label])
         (cons (input* (generate-attrs attrs
                                       :defaults {:type "radio"
                                                  :id (:id attrs)
                                                  :name (:name attrs)
                                                  :value (:value attrs)
                                                  :checked (:checked attrs)
                                                  :on-change (:on-change attrs)
                                                  :disabled (:disabled attrs)}
                                       :depth [:label :input]))
               body)))

(defn page* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* attrs body))

;;;
;;; Layout (Bootstap's grid system)
;;;

(defn fixed-layout* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* (generate-attrs attrs
                              :defaults {:class-name "container"})
         body))

(defn fluid-layout* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* (generate-attrs attrs
                              :defaults {:class-name "container-fluid"})
         body))

(defn row* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* (generate-attrs attrs
                              :defaults {:class-name "row"})
         body))

(defn column*
  "Returns om.dom/div component with its :className set to
  'size-n size-n ...' where size and n are values in the attrs map.
  attrs must be in the form of

  e.g. {:sizes [{:size :sm :cols 6}
                {:size :md :cols 3}
                ...]}"
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* (generate-attrs (dissoc attrs :sizes)
                              :defaults {:class-name (apply validation-state-class-names
                                                            attrs
                                                            (map col-size-css (:sizes attrs)))})
         body))

;; Defines 12 columns controls, column-1* column-2* ... column-12*.
;;
;; e.g. (defn column-7* [attrs & body] ...)
;;
;; Each column maps with bootstrap's grid system columns class names. Attribute
;; available in the attrs map is :size which it can be a value from the
;; sizes map. See defcolumn macro in full-control.core clj namespace.
(defcolumn 1 12)

;;;
;;; navbar
;;;

(defn brand* [attrs & body]
  {:pre [(map? attrs)]}
  {:brand (assoc attrs :body body)})

(defn navbar*
  "Retuns bootstrap's navbar. Attributes available in the attrs map are :class-name."
  [attrs & body]
  {:pre [(map? attrs)]}
  (nav* (generate-attrs attrs
                        :defaults {:role "navigation"
                                   :class-name "navbar navbar-default navbar-static-top"
                                   :display (:display attrs)}
                        :depth [:nav])
        (div* (generate-attrs attrs
                              :defaults {:class-name "container"}
                              :depth [:nav :div])
              (div* (generate-attrs attrs
                                    :defaults {:class-name "navbar-header"}
                                    :depth [:nav :div :div1])
                    (button* (generate-attrs attrs
                                             :defaults {:type "button"
                                                        :data-toggle "collapse"
                                                        :data-target "#navbar-collapse-items"
                                                        :class-name "navbar-toggle collapsed"}
                                             :depth [:nav :div :div1 :button])
                             (span* (generate-attrs attrs
                                                    :defaults {:class-name "icon-bar"}
                                                    :depth [:nav :div :div1 :button :span1]))
                             (span* (generate-attrs attrs
                                                    :defaults {:class-name "icon-bar"}
                                                    :depth [:nav :div :div1 :button :span2]))
                             (span* (generate-attrs attrs
                                                    :defaults {:class-name "icon-bar"}
                                                    :depth [:nav :div :div1 :button :span3])))
                    (let [brand (->> body
                                     (filter :brand)
                                     first
                                     :brand)]
                      (apply a* (generate-attrs (dissoc brand :body)
                                                :defaults {:class-name "navbar-brand"})
                             (:body brand))))
              (apply div* (generate-attrs attrs
                                          :defaults {:id "navbar-collapse-items"
                                                     :class-name "collapse navbar-collapse"}
                                          :depth [:nav :div :div2])
                     (remove :brand body)))))

(defn links-group
  "Returns a series of om.dom/li components inside a om.dom/ul. Basically it
  constructs a menu list from the attrs map parameter. attrs must be in the form of

  e.g. {:links [{:href '#/link1' :body ['link1']}
                {:href '#/link2' :body ['link2' ...] ...}
                ...]}

  Attributes available for each links map are :href, :on-click, :body."
  [attrs]
  {:pre [(map? attrs)]}
  (apply ul* {:class-name (float-class-names attrs "nav navbar-nav")}
         (for [lnk (:links attrs)]
           (li* {}
                (apply a* (dissoc lnk :body) (:body lnk))))))

(defn navbar-btn*
  "Button to render inside the navbar control. Attributes available in the attrs
  map same as the btn* control."
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply btn* (generate-attrs attrs
                              :defaults {:class-name (float-class-names attrs "navbar-btn")})
         body))

;;;
;;; Panels
;;;

(defn panel-header* [attrs & body]
  {:pre [(map? attrs)]}
  {:header (assoc attrs :body body)})

(defn stretch* [attrs & body]
  {:pre [(map? attrs)]}
  {:stretch (assoc attrs :body body)})

(defn panel* [attrs & body]
  {:pre [(map? attrs)]}
  (div* (generate-attrs attrs
                        :defaults {:class-name "panel panel-default"
                                   :display (:display attrs)}
                        :depth [:div])
        (let [header (->> body
                          (filter :header)
                          first
                          :header)]
          (apply div* (generate-attrs (dissoc header :body)
                                      :defaults {:class-name "panel-heading"})
                 (:body header)))
        (if-not (and (= (count body) 1) (:stretch (first body)))
          (apply div* (generate-attrs attrs
                                      :defaults {:class-name "panel-body"}
                                      :depth [:div :div])
                 (remove (some-fn :header :stretch) body)))
        (let [stretch (->> body
                           (filter :stretch)
                           first
                           :stretch)]
          (apply div* (generate-attrs (dissoc stretch :body))
                 (:body stretch)))))

(defn navpanel* [attrs & body]
  {:pre [(map? attrs)]}
  (div* (generate-attrs attrs
                        :defaults {:class-name "panel panel-default"
                                   :display (:display attrs)}
                        :depth [:div])
        (let [header (->> body
                          (filter :header)
                          first
                          :header)]
          (apply div* (generate-attrs (dissoc header :body)
                                      :defaults {:class-name "panel-heading"})
                 (:body header)))
        (div* (generate-attrs attrs
                              :defaults {:class-name "panel-body"}
                              :depth [:div :div])
              (apply div* (generate-attrs attrs
                                          :defaults {:class-name "list-group"}
                                          :depth [:div :div :div])
                     (remove :header body)))))

(defn navpanel-link* [attrs & body]
  {:pre [(map? attrs)]}
  (apply a* (generate-attrs attrs
                            :defaults {:class-name "list-group-item"})
         body))

(defn title1* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h1* (generate-attrs attrs
                             :defaults {:class-name "panel-title"})
         body))

(defn title2* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h2* (generate-attrs attrs
                             :defaults {:class-name "panel-title"})
         body))

(defn title3* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h3* (generate-attrs attrs
                             :defaults {:class-name "panel-title"})
         body))

(defn title4* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h4* (generate-attrs attrs
                             :defaults {:class-name "panel-title"})
         body))

(defn title5* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h5* (generate-attrs attrs
                             :defaults {:class-name "panel-title"})
         body))

;;;
;;; Tables
;;;

(defn grid* [attrs & body]
  {:pre [(map? attrs)]}
  (apply table* (generate-attrs attrs
                                :defaults {:class-name (table-class-names attrs)})
         body))

(defn grid-view* [attrs & body]
  {:pre [(map? attrs)]}
  (table* (generate-attrs attrs
                          :defaults {:class-name (table-class-names attrs)
                                     :display (:display attrs)}
                          :depth [:table])
          (apply tbody* (generate-attrs attrs
                                        :depth [:table :tbody])
                 body)))

;;;
;;; Modals
;;;

(defn modal-header* [attrs & body]
  {:pre [(map? attrs)]}
  {:header (assoc attrs :body body)})

(defn modal-footer* [attrs & body]
  {:pre [(map? attrs)]}
  {:footer (assoc attrs :body body)})

(defn modal* [attrs & body]
  {:pre [(map? attrs)]}
  (div* (generate-attrs attrs
                        :defaults {:class-name "modal fade"
                                   :role "modal"
                                   :display (:display attrs)}
                        :depth [:div]) 
        (div* (generate-attrs attrs
                              :defaults {:class-name "modal-dialog"}
                              :depth [:div :div]) 
              (div* (generate-attrs attrs
                                    :defaults {:class-name "modal-content"}
                                    :depth [:div :div :div])
                    (let [header (->> body
                                      (filter :header)
                                      first
                                      :header)]
                      (apply div* (generate-attrs (dissoc header :body)
                                                  :defaults {:class-name "modal-header"})
                             (:body header)))
                    (apply div* (generate-attrs attrs
                                                :defaults {:class-name "modal-body"}
                                                :depth [:div :div :div :div])
                           (remove (some-fn :header :footer) body))
                    (let [footer (->> body
                                      (filter :footer)
                                      first
                                      :footer)]
                      (apply div* (generate-attrs (dissoc footer :body)
                                                  :defaults {:class-name "modal-footer"})
                             (:body footer)))))))

;;;
;;; Forms
;;;

(deflbl-col 1 12)

(deftxt-col 1 12)

(deftxtarea-col 1 12)

(defdropdown-col 1 12)

(defcheckbox-col 1 12)

(defn help* [attrs & body]
  {:pre [(map? attrs)]}
  (apply span* (generate-attrs attrs
                               :defaults {:class-name "help-block"})
         body))

(defhelp-col 1 12)

(defn form-group* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* (generate-attrs attrs
                              :defaults {:class-name (form-group-class-names attrs)})
         body))

(defn frm* [attrs & body]
  {:pre [(map? attrs)]}
  (form* (generate-attrs attrs
                         :defaults {:display (:display attrs)}
                         :depth [:form])
         (apply fieldset* (generate-attrs attrs
                                          :defaults {:disabled (:disabled attrs)}
                                          :depth [:form :fieldset]) body)))

(defn frm-horizontal* [attrs & body]
  {:pre [(map? attrs)]}
  (apply frm* (generate-attrs attrs
                              :defaults {:class-name "form-horizontal"})
         body))

(defn frm-inline* [attrs & body]
  {:pre [(map? attrs)]}
  (apply frm* (generate-attrs attrs
                              :defaults {:class-name "form-inline"})
         body))
