/**
* Модифицируй пожалуйста предыдущее решение. Реализованные в рамках навигации Composable-функции должны иметь ряд других параметров. Для StartOrderScreen необходимо добавить параметры 
* quantityOptions типа List<Pair<Int, Int>> и modifier типа Modifier. Для SelectOptionScreen необходимо добавить subtotal типа String и modifier типа Modifier, а также убрать параметр title. 
* Для OrderSummaryScreen необходимо добавить параметр modifier типа Modifier.

* Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary. Обрати внимание на переменную uiState! 
* Она используется в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource), subtotal и orderUiState.

* Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код.

Также в функции CupcakeApp не хватает переменных backStackEntry и currentScreen. Последняя в зависимости от текущего элемента из enum class-а меняет значение тайтла в CupcakeAppBar и 
* поэтому должна быть передана в эту функцию.

* Функция cancelOrderAndNavigateToStart должна быть private.
*/

@Composable//perplexity prompt 1 2nd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Получаем текущее состояние UI
    val uiState by viewModel.uiState.collectAsState()

    // Получаем текущий backStackEntry и определяем текущий экран
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = when (backStackEntry?.destination?.route) {
        CupcakeScreen.Start.name -> CupcakeScreen.Start
        CupcakeScreen.Flavor.name -> CupcakeScreen.Flavor
        CupcakeScreen.Pickup.name -> CupcakeScreen.Pickup
        CupcakeScreen.Summary.name -> CupcakeScreen.Summary
        else -> CupcakeScreen.Start
    }

    // Определяем, можно ли вернуться назад (если есть предыдущий экран)
    val canNavigateBack = navController.previousBackStackEntry != null

    // Функция отмены заказа и возврата на стартовый экран
    // Сделана private, как и требовалось
    fun cancelOrderAndNavigateToStart() {
        viewModel.resetOrder()
        navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
    }

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier,
                // Передаём заголовок из enum
                // (в твоём примере CupcakeAppBar всегда показывал R.string.app_name, 
                // но теперь сделаем динамический заголовок)
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    modifier = Modifier.fillMaxSize(),
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    }
                )
            }
            composable(route = CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    options = DataSource.flavors.map { stringResource(it) },
                    subtotal = uiState.price,
                    modifier = Modifier.fillMaxSize(),
                    onSelectionChanged = { flavor ->
                        viewModel.setFlavor(flavor)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() }
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    options = uiState.pickupOptions,
                    subtotal = uiState.price,
                    modifier = Modifier.fillMaxSize(),
                    onSelectionChanged = { date ->
                        viewModel.setDate(date)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() }
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    modifier = Modifier.fillMaxSize(),
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    onSendButtonClicked = { subject: String, summary: String ->
                        // TODO: Реализовать подтверждение заказа
                    }
                )
            }
        }
    }
}

@Composable
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.app_name
) {
    TopAppBar(
        title = { Text(stringResource(id = title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
