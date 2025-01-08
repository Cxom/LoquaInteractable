package net.punchtree.loquainteractable.transit.streetcar.path

data class StreetcarRoute(
    internal val stops: List<StreetcarStop>,
    internal val path: Path,
)
