@Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreate() called with: "
                    + "savedInstanceState = [" + savedInstanceState + "]");
        }

        ThemeHelper.setDayNightMode(this);
        ThemeHelper.setTheme(this, ServiceHelper.getSelectedServiceId(this));

        assureCorrectAppLanguage(this);
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        drawerLayoutBinding = mainBinding.drawerLayout;
        drawerHeaderBinding = DrawerHeaderBinding.bind(drawerLayoutBinding.navigation
                .getHeaderView(0));
        toolbarLayoutBinding = mainBinding.toolbarLayout;
        setContentView(mainBinding.getRoot());

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            initFragments();
        }

        setSupportActionBar(toolbarLayoutBinding.toolbar);
        try {
            setupDrawer();
        } catch (final Exception e) {
            ErrorUtil.showUiErrorSnackbar(this, "Setting up drawer", e);
        }

        if (DeviceUtils.isTv(this)) {
            FocusOverlayView.setupFocusObserver(this);
        }
        openMiniPlayerUponPlayerStarted();

        if (PermissionHelper.checkPostNotificationsPermission(this,
                PermissionHelper.POST_NOTIFICATIONS_REQUEST_CODE)) {
            // Schedule worker for checking for new streams and creating corresponding notifications
            // if this is enabled by the user.
            NotificationWorker.initialize(this);
        }
        // setup bottom navigation
        setUpBottomNavigation();


    }


    private void setUpBottomNavigation() {
        mBottomNavigation.setBehaviorTranslationEnabled(false);
        mBottomNavigation.setTranslucentNavigationEnabled(false);

        // Force to tint the drawable (useful for font with icon for example)
        mBottomNavigation.setForceTint(true);
        // always show title and icon
        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // Change colors

        if (mBottomNavigation != null) {
            mBottomNavigation.setAccentColor(R.color.light_bottom_navigation_accent_color);
        }
        mBottomNavigation.setAccentColor(ThemeHelper.isLightThemeSelected(this)
                ? ContextCompat.getColor(this, R.color.light_bottom_navigation_accent_color)
                :                ContextCompat.getColor(this, R.color.white));
        mBottomNavigation.setDefaultBackgroundColor(
                ThemeHelper.isLightThemeSelected(this)
                ? ContextCompat.getColor(this, R.color.light_bottom_navigation_background_color)
                        :                        ContextCompat.getColor(this,
                        R.color.light_bottom_navigation_accent_color));

        final AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this,
                R.menu.menu_navigation);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigation);

        // onTabSelected listener
        mBottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            final Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_holder);
            switch (position) {
                case 0:
                    if (!(fragment instanceof FeedFragment)) {
                        NavigationHelper.gotoMainFragment(getSupportFragmentManager());
                    }
                    return true;

                case 1:
                    if (!(fragment instanceof FeedFragment)) {
                        NavigationHelper.openSubscriptionFragment(getSupportFragmentManager());
                    }
                    return true;

                case 2:
                    if (!(fragment instanceof FeedFragment)) {
                        NavigationHelper.openFeedFragment(getSupportFragmentManager());
                    }
                    return true;

//                case 3:
//                    if (!(fragment instanceof FeedFragment)) {
//                        NavigationHelper.openFeedFragment(getSupportFragmentManager());
//                    }
//                    return true;
//
//                case 4:
//                    if (!(fragment instanceof FeedFragment)) {
//                        NavigationHelper.openFeedFragment(getSupportFragmentManager());
//                    }
//                    return true;

            }
            return false;
        });
    }
