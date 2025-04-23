/**
*Модифицируй пожалуйста предыдущее решение. В функции OrderSummaryScreen вместо параметра onNextButtonClicked должен использоваться onSendButtonClicked. 
*Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary.

* Также обрати пожалуйста внимание на пременную uiState. С её помощью должны передаваться другие параметры. Какие должны можно увидеть в OrderUiState.kt. 
* Также viewModel вызывает несуществующий метод в параметре onSelectionChanged (где route Pickup.name). Необходимо найти похожий метод в OrderViewModel.kt и использовать его
* В функции CupcakeApp не хватает переменных backStackEntry и currentScreen. Последняя в зависимости от текущего элемента из enum class-a меняет значение тайтла в CupcakeAppBar и поэтому 
* должна быть передана в эту функцию
*/

// Kotlin GitHub Copilot GPT3mini prompt 1 2nd attempt

enum class CupcakeScreen(val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}

@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    
    Scaffold(
        topBar = {
            val canNavigateBack = navController.previousBackStackEntry != null
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = canNavigateBack,
                navigateUp = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CupcakeScreen.Start.name) {
                // Для стартового экрана используем опции из DataSource
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = DataSource.flavors,
                    onSelectionChanged = { flavor -> viewModel.setFlavor(flavor) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { date -> viewModel.setDate(date) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onSendButtonClicked = { subject: String, summary: String ->
                        // TODO: реализовать отправку заказа с использованием темы (subject) и текста (summary)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

private fun cancelOrderAndNavigateToStart(
    navController: NavHostController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.navigate(CupcakeScreen.Start.name) {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
    }
}
