// !The running version of the code!
@Composable //GitHub CL prompt 1 1st attempt
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
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(route = CupcakeScreen.Flavor.name) {
                val flavors = DataSource.flavors.map { stringResource(it) }
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = flavors,
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    }
                )
            }

            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    }
                )
            }

            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        // TODO: Handle order submission
                    }
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
        popUpTo(CupcakeScreen.Start.name) {
            inclusive = true
        }
    }
}
