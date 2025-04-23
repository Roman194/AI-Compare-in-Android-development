// Kotlin GitHub Copilot GPT3mini prompt 1 1st attempt
@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            // Если возможно возвращение назад, передаём navigateUp, которое вызывает popBackStack
            val canNavigateBack = navController.previousBackStackEntry != null
            CupcakeAppBar(
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
                StartOrderScreen(
                    quantityOptions = uiState.quantityOptions,
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    subtotal = uiState.subtotal,
                    options = uiState.flavors,
                    onSelectionChanged = { flavor -> viewModel.setFlavor(flavor) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.subtotal,
                    options = uiState.pickupDates,
                    onSelectionChanged = { date -> viewModel.setPickupDate(date) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onNextButtonClicked = { /* TODO: реализовать подтверждение заказа */ },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// Kotlin
private fun cancelOrderAndNavigateToStart(
    navController: NavHostController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.navigate(CupcakeScreen.Start.name) {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
    }
}
