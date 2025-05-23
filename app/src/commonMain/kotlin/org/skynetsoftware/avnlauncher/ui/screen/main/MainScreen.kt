package org.skynetsoftware.avnlauncher.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.dokar.sonner.LocalToastContentColor
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.skynetsoftware.avnlauncher.LocalExitApplication
import org.skynetsoftware.avnlauncher.LocalNavigator
import org.skynetsoftware.avnlauncher.LocalWindowControl
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.close
import org.skynetsoftware.avnlauncher.app.generated.resources.fullscreen
import org.skynetsoftware.avnlauncher.app.generated.resources.fullscreen_exit
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationCheckForUpdates
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationExit
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationImport
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationMaximizeFloating
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationSettings
import org.skynetsoftware.avnlauncher.app.generated.resources.hoverExplanationSfw
import org.skynetsoftware.avnlauncher.app.generated.resources.import
import org.skynetsoftware.avnlauncher.app.generated.resources.refresh
import org.skynetsoftware.avnlauncher.app.generated.resources.settings
import org.skynetsoftware.avnlauncher.app.generated.resources.toolbarActionNsfw
import org.skynetsoftware.avnlauncher.app.generated.resources.toolbarActionSfw
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesDisplayMode
import org.skynetsoftware.avnlauncher.domain.model.GridColumns
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.imageloader.ImageLoaderFactory
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.State
import org.skynetsoftware.avnlauncher.state.buildText
import org.skynetsoftware.avnlauncher.ui.component.IconAction
import org.skynetsoftware.avnlauncher.ui.component.Search
import org.skynetsoftware.avnlauncher.ui.component.TextAction
import org.skynetsoftware.avnlauncher.ui.screen.PickExecutableDialog
import org.skynetsoftware.avnlauncher.ui.screen.main.games.Games
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModel
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.updatechecker.buildToastMessage
import org.skynetsoftware.avnlauncher.utils.collectAsMutableState
import java.text.SimpleDateFormat

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MainScreen(
    gamesViewModel: MainScreenViewModel = viewModel(),
    imageLoaderFactory: ImageLoaderFactory = koinInject(),
) {
    val games by remember { gamesViewModel.games }.collectAsState()
    val currentFilter by remember { gamesViewModel.selectedFilter }.collectAsState(Filter.All)
    val filters by remember { gamesViewModel.filters }.collectAsState(emptyList<FilterViewItem>())
    val currentSortOrder by remember { gamesViewModel.sortOrder }.collectAsState()
    val currentSortDirection by remember { gamesViewModel.sortDirection }.collectAsState()
    val currentGamesDisplayMode by remember { gamesViewModel.gamesDisplayMode }.collectAsState()
    val sfwMode by remember { gamesViewModel.sfwMode }.collectAsState()

    var showExecutablePathPicker by gamesViewModel.showExecutablePathPicker.collectAsMutableState()

    val totalPlayTime by remember { gamesViewModel.totalPlayTime }.collectAsState()
    val averagePlayTime by remember { gamesViewModel.averagePlayTime }.collectAsState()
    val newUpdateAvailableIndicatorVisible by remember {
        gamesViewModel.newUpdateAvailableIndicatorVisible
    }.collectAsState()
    var searchQuery by remember { gamesViewModel.searchQuery }
        .collectAsMutableState(context = Dispatchers.Main.immediate)
    val globalState by remember { gamesViewModel.state }.collectAsState()
    val imageAspectRatio by remember { gamesViewModel.imageAspectRatio }.collectAsState()
    val dateFormat by remember { gamesViewModel.dateFormat }.collectAsState()
    val timeFormat by remember { gamesViewModel.timeFormat }.collectAsState()
    val gridColumns by remember { gamesViewModel.gridColumns }.collectAsState()

    val gameRunning by remember { gamesViewModel.gameRunning }.collectAsState()

    setSingletonImageLoaderFactory { context ->
        imageLoaderFactory.createImageLoader(false, context)
    }

    val toasterState = rememberToasterState()

    LaunchedEffect(null) {
        gamesViewModel.toastMessage.collect {
            it?.let { toastMessage ->
                toasterState.show(
                    message = toastMessage,
                )
            }
        }
    }

    val blockedByPopup = LocalWindowControl.current?.blockedByPopup
    val blurModifier = if (blockedByPopup == true) {
        Modifier.blur(3.dp)
    } else {
        Modifier
    }
    Surface(
        modifier = blurModifier.fillMaxSize(),
    ) {
        MainScreenContent(
            games = games,
            runningGame = gameRunning,
            currentFilter = currentFilter,
            filters = filters,
            currentSortOrder = currentSortOrder,
            currentSortDirection = currentSortDirection,
            currentGamesDisplayMode = currentGamesDisplayMode,
            newUpdateAvailableIndicatorVisible = newUpdateAvailableIndicatorVisible,
            globalState = globalState,
            sfwMode = sfwMode,
            totalPlayTime = totalPlayTime,
            averagePlayTime,
            searchQuery,
            imageAspectRatio = imageAspectRatio,
            dateFormat = SimpleDateFormat(dateFormat),
            timeFormat = SimpleDateFormat(timeFormat),
            gridColumns = gridColumns,
            setSearchQuery = {
                searchQuery = it
            },
            startUpdateCheck = gamesViewModel::startUpdateCheck,
            toggleSfwMode = gamesViewModel::toggleSfwMode,
            setFilter = {
                gamesViewModel.setFilter(it)
                if (it == Filter.GamesWithUpdate) {
                    gamesViewModel.resetNewUpdateAvailableIndicatorVisible()
                }
            },
            setSortOrder = gamesViewModel::setSortOrder,
            setSortDirection = gamesViewModel::setSortDirection,
            setGamesDisplayMode = gamesViewModel::setGamesDisplayMode,
            launchGame = gamesViewModel::launchGame,
            stopGame = gamesViewModel::stopGame,
            resetUpdateAvailable = gamesViewModel::resetUpdateAvailable,
            updateRating = gamesViewModel::updateRating,
        )
    }

    showExecutablePathPicker?.let { game ->
        PickExecutableDialog(
            executablePaths = game.executablePaths,
            onCloseRequest = {
                showExecutablePathPicker = null
            },
            onExecutablePicked = { executablePath ->
                showExecutablePathPicker = null
                gamesViewModel.launchGame(game, executablePath)
            },
        )
    }

    @Suppress("SpreadOperator")
    Toaster(
        state = toasterState,
        showCloseButton = true,
        darkTheme = true,
        messageSlot = { toast ->
            val message = toast.message as? Event.ToastMessage<*>
            val text = when (message?.message) {
                is String -> message.message
                is StringResource -> stringResource(message.message, *message.args)
                is UpdateCheckResult -> message.message.buildToastMessage()
                else -> null
            }
            if (text != null) {
                val contentColor = LocalToastContentColor.current
                BasicText(text, color = { contentColor })
            }
        },
        background = {
            SolidColor(MaterialTheme.colors.surface)
        },
    )
}

@Composable
private fun Toolbar(
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    onSfwModeClicked: () -> Unit,
) {
    val draggableArea = LocalWindowControl.current?.draggableArea
    if (draggableArea != null) {
        draggableArea.invoke {
            ToolbarInternal(
                globalState = globalState,
                sfwMode = sfwMode,
                totalPlayTime = totalPlayTime,
                averagePlayTime = averagePlayTime,
                searchQuery = searchQuery,
                setSearchQuery = setSearchQuery,
                startUpdateCheck = startUpdateCheck,
                onSfwModeClicked = onSfwModeClicked,
            )
        }
    } else {
        ToolbarInternal(
            globalState = globalState,
            sfwMode = sfwMode,
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
            startUpdateCheck = startUpdateCheck,
            onSfwModeClicked = onSfwModeClicked,
        )
    }
}

@Composable
private fun ToolbarInternal(
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    onSfwModeClicked: () -> Unit,
) {
    var maximized by remember { mutableStateOf(false) }
    TopAppBar {
        val windowControl = LocalWindowControl.current
        ToolbarTitle(
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
        )
        Search(
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
        )

        if (globalState != State.Idle) {
            Text(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(end = 10.dp),
                textAlign = TextAlign.End,
                text = globalState.buildText(),
                style = MaterialTheme.typography.subtitle2,
            )
        } else {
            Spacer(
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.End,
        ) {
            val exitApplication = LocalExitApplication.current
            val navigator = LocalNavigator.current
            TextAction(
                text = stringResource(if (sfwMode) Res.string.toolbarActionSfw else Res.string.toolbarActionNsfw),
                hoverExplanation = stringResource(Res.string.hoverExplanationSfw),
            ) {
                onSfwModeClicked()
            }
            IconAction(
                icon = Res.drawable.import,
                hoverExplanation = stringResource(Res.string.hoverExplanationImport),
            ) {
                navigator?.navigateToImportGame()
            }
            IconAction(
                icon = Res.drawable.refresh,
                hoverExplanation = stringResource(Res.string.hoverExplanationCheckForUpdates),
            ) {
                startUpdateCheck()
            }
            IconAction(
                icon = Res.drawable.settings,
                hoverExplanation = stringResource(Res.string.hoverExplanationSettings),
            ) {
                navigator?.navigateToSettings()
            }
            IconAction(
                icon = if (maximized) Res.drawable.fullscreen_exit else Res.drawable.fullscreen,
                hoverExplanation = stringResource(Res.string.hoverExplanationMaximizeFloating),
            ) {
                maximized = !maximized
                if (maximized) {
                    windowControl?.maximizeWindow?.invoke()
                } else {
                    windowControl?.floatingWindow?.invoke()
                }
            }
            IconAction(
                icon = Res.drawable.close,
                hoverExplanation = stringResource(Res.string.hoverExplanationExit),
            ) {
                exitApplication?.invoke()
            }
        }
    }
}

@Composable
fun MainScreenContent(
    games: List<Game>,
    runningGame: Game?,
    currentFilter: Filter,
    filters: List<FilterViewItem>,
    currentSortOrder: SortOrder,
    currentSortDirection: SortDirection,
    currentGamesDisplayMode: GamesDisplayMode,
    newUpdateAvailableIndicatorVisible: Boolean,
    globalState: State,
    sfwMode: Boolean,
    totalPlayTime: Long,
    averagePlayTime: Float,
    searchQuery: String,
    imageAspectRatio: Float,
    dateFormat: SimpleDateFormat,
    timeFormat: SimpleDateFormat,
    gridColumns: GridColumns,
    setSearchQuery: (searchQuery: String) -> Unit,
    startUpdateCheck: () -> Unit,
    toggleSfwMode: () -> Unit,
    setFilter: (filter: Filter) -> Unit,
    setSortOrder: (sortOrder: SortOrder) -> Unit,
    setSortDirection: (sortDirection: SortDirection) -> Unit,
    setGamesDisplayMode: (gamesDisplayMode: GamesDisplayMode) -> Unit,
    launchGame: (game: Game) -> Unit,
    stopGame: () -> Unit,
    resetUpdateAvailable: (availableVersion: String, game: Game) -> Unit,
    updateRating: (rating: Int, game: Game) -> Unit,
) {
    val navigator = LocalNavigator.current
    Column {
        Toolbar(
            globalState = globalState,
            sfwMode = sfwMode,
            totalPlayTime = totalPlayTime,
            averagePlayTime = averagePlayTime,
            searchQuery = searchQuery,
            setSearchQuery = setSearchQuery,
            startUpdateCheck = startUpdateCheck,
            onSfwModeClicked = toggleSfwMode,
        )
        SortFilter(
            games = games,
            currentFilter = currentFilter,
            filters = filters,
            currentSortOrder = currentSortOrder,
            currentSortDirection = currentSortDirection,
            currentGamesDisplayMode = currentGamesDisplayMode,
            updateAvailableIndicatorVisible = newUpdateAvailableIndicatorVisible,
            modifier = Modifier.align(Alignment.End).padding(10.dp),
            setFilter = setFilter,
            setSortOrder = setSortOrder,
            setSortDirection = setSortDirection,
            setGamesDisplayMode = setGamesDisplayMode,
        )
        Games(
            games = games,
            runningGame = runningGame,
            sfwMode = sfwMode,
            query = searchQuery,
            imageAspectRatio = imageAspectRatio,
            dateFormat = dateFormat,
            timeFormat = timeFormat,
            gridColumns = gridColumns,
            gamesDisplayMode = currentGamesDisplayMode,
            gameDetails = {
                navigator?.navigateToGameDetails(it)
            },
            launchGame = launchGame,
            stopGame = stopGame,
            resetUpdateAvailable = resetUpdateAvailable,
            updateRating = updateRating,
        )
    }
}
