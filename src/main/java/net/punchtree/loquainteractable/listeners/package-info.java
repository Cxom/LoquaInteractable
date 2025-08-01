package net.punchtree.loquainteractable.listeners;

/*
 * Listeners are complicated. A single event often needs to be observed by many systems,
 * and much of the power of events is the ability for the systems to not have to be managed altogether.
 *
 * Unfortunately, as systems increase, the order in which they all get to process the events becomes
 * more and more important, particularly in enabling debugging without headbanging.
 *
 * This package is for TOP LEVEL event listeners. They should be as simple as possible, and should
 * delegate any complexity to other systems.
 *
 * But having them here serves as a single source of truth for the order in which events are
 * TODO move ALL Listener implementations into this package
 */