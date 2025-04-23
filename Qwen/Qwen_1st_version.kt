@Composable//Qwen prompt 1 1st attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Состояние заказа
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // NavHost для управления навигацией
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CupcakeScreen.Start.name) {
                StartOrderScreen(
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    }
                )
            }

            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    title = R.string.flavor,
                    options = uiState.flavors,
                    onSelectionChanged = { flavor ->
                        viewModel.setFlavor(flavor)
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
                )
            }

            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    title = R.string.choose_pickup_date,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { date ->
                        viewModel.setDate(date)
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
                )
            }

            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    onConfirmButtonClicked = { /* TODO: implement confirmation logic */ }
                )
            }
        }
    }
}

// Функция для отмены заказа и возврата на стартовый экран
private fun cancelOrderAndNavigateToStart(
    navController: NavHostController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
