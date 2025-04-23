@Composable //DeepSeek prompt 1 1st attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Получаем текущий back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Определяем текущий экран
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    // Функция для отмены заказа и возврата на стартовый экран
    fun cancelOrderAndNavigateToStart() {
        viewModel.resetOrder()
        navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
    }

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Стартовый экран
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран выбора вкуса
            composable(route = CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    options = viewModel.flavorOptions,
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    onNextButtonClicked = { 
                        navController.navigate(CupcakeScreen.Pickup.name) 
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран выбора даты
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    options = viewModel.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    onNextButtonClicked = { 
                        navController.navigate(CupcakeScreen.Summary.name) 
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран подтверждения заказа
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    onSendButtonClicked = { orderDetails ->
                        // TODO: Implement send order logic
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
