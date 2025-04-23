@Composable //Yandex prompt 1 1st attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name
        ) {
            composable(CupcakeScreen.Start.name) {
                StartOrderScreen(
                    onNextButtonClicked = { quantity ->
                        if (quantity > 0) {
                            navController.navigate(CupcakeScreen.Flavor.name)
                        }
                    }
                )
            }
            
            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    title = stringResource(R.string.flavor),
                    options = listOf("Vanilla", "Chocolate", "Red Velvet", "Carrot"),
                    onNextButtonClicked = { flavor ->
                        viewModel.setFlavor(flavor)
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
                )
            }
            
            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    title = stringResource(R.string.choose_pickup_date),
                    options = getAvailableDates(),
                    onNextButtonClicked = { date ->
                        viewModel.setPickupDate(date)
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
                )
            }
            
            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    order = uiState,
                    onConfirmButtonClicked = { /* TODO: Implement order confirmation */ },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
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
    navController.popBackStack()
    navController.navigate(CupcakeScreen.Start.name)
}

private fun getAvailableDates(): List<String> {
    // Реализация получения доступных дат
    return listOf("Today", "Tomorrow", "2 days", "3 days")
}
