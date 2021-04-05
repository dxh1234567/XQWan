package com.jj.base.utils

import com.jj.base.exts.printLog
import com.airbnb.epoxy.*


/** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
    CarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}


inline fun EpoxyRecyclerView.getController(): EpoxyController {
    val field = EpoxyRecyclerView::class.java
        .getDeclaredField("epoxyController")
        .apply {
            isAccessible = true
        }
    return field.get(this) as EpoxyController
}

inline fun EpoxyRecyclerView.clearModelBuildListeners() {
    try {
        val field = EpoxyControllerAdapter::class.java
            .getDeclaredField("modelBuildListeners")
            .apply {
                isAccessible = true
            }
        (field.get(getController().adapter) as? ArrayList<*>)?.clear()
    } catch (e: Throwable) {
        printLog {
            LogUtil.d("tagg", "err ${e.message}")
        }
    }
}