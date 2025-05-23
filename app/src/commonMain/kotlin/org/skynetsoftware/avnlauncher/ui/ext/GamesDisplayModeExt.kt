package org.skynetsoftware.avnlauncher.ui.ext

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.grid_view
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationGridDisplayMode
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationListDisplayMode
import org.skynetsoftware.avnlauncher.app.generated.resources.view_list
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode

fun GamesDisplayMode.iconRes(): DrawableResource {
    return when (this) {
        GamesDisplayMode.Grid -> Res.drawable.grid_view
        GamesDisplayMode.List -> Res.drawable.view_list
    }
}

fun GamesDisplayMode.hoverExplanation(): StringResource {
    return when (this) {
        GamesDisplayMode.Grid -> Res.string.hoverExplanationGridDisplayMode
        GamesDisplayMode.List -> Res.string.hoverExplanationListDisplayMode
    }
}
