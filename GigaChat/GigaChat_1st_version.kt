@Composable //GigaChat prompt 1 1st attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val currentScreen = when (navController.currentBackStackEntry?.destination?.route) {
        CupcakeScreen.Start.name -> CupcakeScreen.Start
        CupcakeScreen.Flavor.name -> CupcakeScreen.Flavor
        CupcakeScreen.Pickup.name -> CupcakeScreen.Pickup
        CupcakeScreen.Summary.name -> CupcakeScreen.Summary
        else -> CupcakeScreen.Start
    }
    
    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentTitle = stringResource(currentScreen.title)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = CupcakeScreen.Start.name
            ) {
                composable(CupcakeScreen.Start.name) {
                    StartOrderScreen(
                        quantitySelectionHandler = { selectedQuantity ->
                            viewModel.setQuantity(selectedQuantity)
                            navController.navigate(CupcakeScreen.Flavor.name)
                        }
                    )
                }
                
                composable(CupcakeScreen.Flavor.name) {
                    SelectOptionScreen(
                        options = viewModel.availableFlavors,
                        optionSelectedHandler = { selectedFlavor ->
                            viewModel.selectFlavor(selectedFlavor)
                            navController.navigate(CupcakeScreen.Pickup.name)
                        },
                        cancellationHandler = ::cancelOrderAndNavigateToStart
                    )
                }
                
                composable(CupcakeScreen.Pickup.name) {
                    SelectOptionScreen(
                        options = viewModel.availablePickupDates,
                        optionSelectedHandler = { selectedDate ->
                            viewModel.selectPickupDate(selectedDate)
                            navController.navigate(CupcakeScreen.Summary.name)
                        },
                        cancellationHandler = ::cancelOrderAndNavigateToStart
                    )
                }
                
                composable(CupcakeScreen.Summary.name) {
                    OrderSummaryScreen(
                        orderDetails = viewModel.getOrderDetails(),
                        confirmationHandler = {},
                        cancellationHandler = ::cancelOrderAndNavigateToStart
                    )
                }
            }
        }
    }
}

@Composable
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentTitle: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = currentTitle) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_button))
                }
            }
        }
    )
}

private fun cancelOrderAndNavigateToStart(viewModel: OrderViewModel, navController: NavHostController) {
    viewModel.resetOrder()
    navController.popBackStack(route = CupcakeScreen.Start.name, inclusive = false)
}
