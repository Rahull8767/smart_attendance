import os

layout_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main\res\layout"

layout_home = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/provider_background">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavProvider">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">

            <TextView
                android:id="@+id/tvGreeting"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Good morning, Provider"
                android:textColor="@color/provider_text_primary"
                android:textSize="28sp"
                android:textStyle="bold" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Platform overview"
                android:textColor="@color/provider_text_secondary"
                android:textSize="16sp"
                android:layout_marginTop="8dp"
                android:layout_marginBottom="24dp"/>

            <!-- KPIs -->
            <GridLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:columnCount="2"
                android:rowCount="2">

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginEnd="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface_elevated"
                    android:padding="16dp"
                    android:orientation="vertical">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="TOTAL CEOs"
                        android:textColor="@color/provider_text_secondary"
                        android:textSize="12sp"
                        android:textStyle="bold"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="42"
                        android:textColor="@color/provider_primary"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        android:layout_marginTop="4dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Active organizations"
                        android:textColor="@color/provider_text_muted"
                        android:textSize="12sp"
                        android:layout_marginTop="4dp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginStart="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface_elevated"
                    android:padding="16dp"
                    android:orientation="vertical">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="TOTAL EMPLOYEES"
                        android:textColor="@color/provider_text_secondary"
                        android:textSize="12sp"
                        android:textStyle="bold"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="1,240"
                        android:textColor="@color/provider_primary"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        android:layout_marginTop="4dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Across all orgs"
                        android:textColor="@color/provider_text_muted"
                        android:textSize="12sp"
                        android:layout_marginTop="4dp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginEnd="8dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="ACTIVE SITES"
                        android:textColor="@color/provider_text_secondary"
                        android:textSize="12sp"
                        android:textStyle="bold"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="128"
                        android:textColor="@color/provider_text_primary"
                        android:textSize="20sp"
                        android:textStyle="bold"
                        android:layout_marginTop="4dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Across platform"
                        android:textColor="@color/provider_text_muted"
                        android:textSize="12sp"
                        android:layout_marginTop="4dp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginStart="8dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="TODAY'S ATTENDANCE"
                        android:textColor="@color/provider_text_secondary"
                        android:textSize="12sp"
                        android:textStyle="bold"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="94%"
                        android:textColor="@color/provider_text_primary"
                        android:textSize="20sp"
                        android:textStyle="bold"
                        android:layout_marginTop="4dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Platform average"
                        android:textColor="@color/provider_text_muted"
                        android:textSize="12sp"
                        android:layout_marginTop="4dp"/>
                </LinearLayout>

            </GridLayout>

            <!-- Quick Actions -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Quick Actions"
                android:textColor="@color/provider_text_primary"
                android:textSize="18sp"
                android:textStyle="bold"
                android:layout_marginTop="32dp"
                android:layout_marginBottom="16dp"/>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical">
                <Button
                    android:id="@+id/btnCreateCeo"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:layout_height="56dp"
                    android:text="+ CREATE CEO"
                    android:backgroundTint="@color/provider_primary"
                    android:textColor="@color/provider_background"
                    android:layout_marginEnd="8dp"/>
                
                <Button
                    android:id="@+id/btnViewCeos"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:layout_height="56dp"
                    android:text="VIEW CEOs"
                    android:backgroundTint="@color/provider_surface_secondary"
                    android:textColor="@color/provider_text_primary"
                    android:layout_marginStart="8dp"/>
            </LinearLayout>

            <!-- Platform Health -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Platform Health"
                android:textColor="@color/provider_text_primary"
                android:textSize="18sp"
                android:textStyle="bold"
                android:layout_marginTop="32dp"
                android:layout_marginBottom="16dp"/>
                
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/provider_surface"
                android:padding="16dp"
                android:orientation="horizontal"
                android:gravity="center_vertical">
                <TextView
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:layout_height="wrap_content"
                    android:text="Face Verification"
                    android:textColor="@color/provider_text_primary"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="98%"
                    android:textColor="@color/provider_success"
                    android:textStyle="bold"
                    android:layout_marginEnd="8dp"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Healthy"
                    android:textColor="@color/provider_text_secondary"
                    android:textSize="12sp"/>
            </LinearLayout>
            
            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:background="@color/provider_border"/>
                
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/provider_surface"
                android:padding="16dp"
                android:orientation="horizontal"
                android:gravity="center_vertical">
                <TextView
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:layout_height="wrap_content"
                    android:text="Location Verification"
                    android:textColor="@color/provider_text_primary"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="96%"
                    android:textColor="@color/provider_success"
                    android:textStyle="bold"
                    android:layout_marginEnd="8dp"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Healthy"
                    android:textColor="@color/provider_text_secondary"
                    android:textSize="12sp"/>
            </LinearLayout>
            
            <!-- Recent Activity -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Recent Activity"
                android:textColor="@color/provider_text_primary"
                android:textSize="18sp"
                android:textStyle="bold"
                android:layout_marginTop="32dp"
                android:layout_marginBottom="16dp"/>
                
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/provider_surface"
                android:padding="16dp"
                android:orientation="vertical">
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Rahul Sharma • ABC Developers"
                    android:textColor="@color/provider_text_primary"
                    android:textStyle="bold"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="CEO created • 10m ago"
                    android:textColor="@color/provider_text_secondary"
                    android:textSize="12sp"
                    android:layout_marginTop="4dp"/>
            </LinearLayout>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/provider_surface"
                android:padding="16dp"
                android:layout_marginTop="8dp"
                android:orientation="vertical">
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Jane Smith • XYZ Logistics"
                    android:textColor="@color/provider_text_primary"
                    android:textStyle="bold"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="New site created • 1h ago"
                    android:textColor="@color/provider_text_secondary"
                    android:textSize="12sp"
                    android:layout_marginTop="4dp"/>
            </LinearLayout>

            <!-- Alerts Preview -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_marginTop="32dp"
                android:layout_marginBottom="16dp"
                android:gravity="center_vertical">
                <TextView
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:layout_height="wrap_content"
                    android:text="Alerts"
                    android:textColor="@color/provider_text_primary"
                    android:textSize="18sp"
                    android:textStyle="bold"/>
                <TextView
                    android:id="@+id/btnAlerts"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="VIEW ALL"
                    android:textColor="@color/provider_primary"
                    android:textStyle="bold"
                    android:clickable="true"
                    android:focusable="true"/>
            </LinearLayout>
            
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/provider_surface"
                android:padding="16dp"
                android:orientation="vertical">
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">
                    <View
                        android:layout_width="8dp"
                        android:layout_height="8dp"
                        android:layout_gravity="center_vertical"
                        android:background="@color/provider_warning"
                        android:layout_marginEnd="8dp"/>
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Verification failures spike"
                        android:textColor="@color/provider_text_primary"
                        android:textStyle="bold"/>
                </LinearLayout>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Global Corp • 2h ago"
                    android:textColor="@color/provider_text_secondary"
                    android:textSize="12sp"
                    android:layout_marginTop="4dp"
                    android:layout_marginStart="16dp"/>
            </LinearLayout>

            <Space
                android:layout_width="match_parent"
                android:layout_height="40dp"/>

        </LinearLayout>
    </ScrollView>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavProvider"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/provider_surface"
        app:itemIconTint="@color/nav_active"
        app:itemTextColor="@color/nav_active"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_provider" />

</androidx.constraintlayout.widget.ConstraintLayout>
"""

layout_ceo_list = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:orientation="vertical"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavProvider">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp"
            android:background="@color/provider_surface_secondary">
            
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="CEOs"
                android:textColor="@color/provider_text_primary"
                android:textSize="28sp"
                android:textStyle="bold" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Manage organizations using your platform"
                android:textColor="@color/provider_text_secondary"
                android:textSize="14sp"
                android:layout_marginTop="4dp"
                android:layout_marginBottom="16dp"/>

            <EditText
                android:layout_width="match_parent"
                android:layout_height="48dp"
                android:background="@color/provider_surface"
                android:hint="Search CEO or company..."
                android:textColorHint="@color/provider_text_muted"
                android:textColor="@color/provider_text_primary"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:drawableStart="@android:drawable/ic_menu_search"
                android:drawablePadding="8dp"
                android:layout_marginBottom="12dp"/>
                
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Filter: All"
                    android:padding="8dp"
                    android:textColor="@color/provider_text_primary"
                    android:background="@color/provider_surface"
                    android:layout_marginEnd="8dp"/>
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Sort: Recently active"
                    android:padding="8dp"
                    android:textColor="@color/provider_text_primary"
                    android:background="@color/provider_surface"/>
            </LinearLayout>
        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">
                
                <!-- Dummy CEO Card -->
                <LinearLayout
                    android:id="@+id/btnCeoCard"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:clickable="true"
                    android:focusable="true">
                    
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">
                        
                        <ImageView
                            android:layout_width="48dp"
                            android:layout_height="48dp"
                            android:background="@color/provider_surface_elevated"
                            android:src="@android:drawable/ic_menu_camera"/>
                            
                        <LinearLayout
                            android:layout_width="0dp"
                            android:layout_weight="1"
                            android:layout_height="wrap_content"
                            android:orientation="vertical"
                            android:layout_marginStart="12dp">
                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="Rahul Sharma"
                                android:textColor="@color/provider_text_primary"
                                android:textSize="18sp"
                                android:textStyle="bold"/>
                            <TextView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="ABC Developers"
                                android:textColor="@color/provider_text_secondary"
                                android:textSize="14sp"/>
                        </LinearLayout>
                        
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="ACTIVE"
                            android:textColor="@color/provider_success"
                            android:textSize="12sp"
                            android:textStyle="bold"
                            android:background="#1A22C55E"
                            android:paddingHorizontal="8dp"
                            android:paddingVertical="4dp"/>
                    </LinearLayout>
                    
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:layout_marginTop="16dp">
                        <TextView
                            android:layout_width="0dp"
                            android:layout_weight="1"
                            android:layout_height="wrap_content"
                            android:text="48 employees"
                            android:textColor="@color/provider_text_primary"/>
                        <TextView
                            android:layout_width="0dp"
                            android:layout_weight="1"
                            android:layout_height="wrap_content"
                            android:text="6 sites"
                            android:textColor="@color/provider_text_primary"/>
                    </LinearLayout>
                    
                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Active 12 min ago"
                        android:textColor="@color/provider_text_muted"
                        android:textSize="12sp"
                        android:layout_marginTop="8dp"/>
                </LinearLayout>
            </LinearLayout>
        </ScrollView>
    </LinearLayout>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavProvider"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/provider_surface"
        app:itemIconTint="@color/nav_active"
        app:itemTextColor="@color/nav_active"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_provider" />

</androidx.constraintlayout.widget.ConstraintLayout>
"""

layout_ceo_details = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="@color/provider_surface_secondary">
        <ImageView
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@android:drawable/ic_menu_revert"
            android:padding="8dp"
            android:clickable="true"
            android:focusable="true"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="CEO Details"
            android:textColor="@color/provider_text_primary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginStart="16dp"/>
    </LinearLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">
            
            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_gravity="center"
                android:src="@android:drawable/ic_menu_camera"
                android:background="@color/provider_surface"/>
                
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Rahul Sharma"
                android:textColor="@color/provider_text_primary"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_gravity="center"
                android:layout_marginTop="12dp"/>
                
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="ABC Developers"
                android:textColor="@color/provider_text_secondary"
                android:textSize="16sp"
                android:layout_gravity="center"
                android:layout_marginTop="4dp"/>
                
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="ACTIVE"
                android:textColor="@color/provider_success"
                android:textSize="12sp"
                android:textStyle="bold"
                android:background="#1A22C55E"
                android:paddingHorizontal="12dp"
                android:paddingVertical="4dp"
                android:layout_gravity="center"
                android:layout_marginTop="8dp"/>
                
            <GridLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:columnCount="2"
                android:rowCount="2"
                android:layout_marginTop="24dp">
                
                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginEnd="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical"
                    android:gravity="center">
                    <TextView android:text="Employees" android:textColor="@color/provider_text_secondary" android:textSize="12sp"/>
                    <TextView android:text="48" android:textColor="@color/provider_text_primary" android:textSize="20sp" android:textStyle="bold" android:layout_marginTop="4dp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginStart="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical"
                    android:gravity="center">
                    <TextView android:text="Sites" android:textColor="@color/provider_text_secondary" android:textSize="12sp"/>
                    <TextView android:text="6" android:textColor="@color/provider_text_primary" android:textSize="20sp" android:textStyle="bold" android:layout_marginTop="4dp"/>
                </LinearLayout>
            </GridLayout>
            
            <TextView android:text="ORGANIZATION" android:textColor="@color/provider_text_secondary" android:textStyle="bold" android:layout_marginTop="24dp" android:layout_marginBottom="8dp"/>
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:background="@color/provider_surface" android:padding="16dp" android:orientation="vertical">
                <TextView android:text="Industry: Construction" android:textColor="@color/provider_text_primary" android:layout_marginBottom="8dp"/>
                <TextView android:text="Email: admin@abcdev.com" android:textColor="@color/provider_text_primary" android:layout_marginBottom="8dp"/>
                <TextView android:text="Phone: +91 9876543210" android:textColor="@color/provider_text_primary"/>
            </LinearLayout>
            
            <TextView android:text="ACTIONS" android:textColor="@color/provider_text_secondary" android:textStyle="bold" android:layout_marginTop="24dp" android:layout_marginBottom="8dp"/>
            <Button
                android:id="@+id/btnViewEmployees"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="View Employees"
                android:backgroundTint="@color/provider_surface"
                android:textColor="@color/provider_text_primary"
                android:layout_marginBottom="8dp"/>
            <Button
                android:id="@+id/btnViewAnalytics"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="View Analytics"
                android:backgroundTint="@color/provider_surface"
                android:textColor="@color/provider_text_primary"
                android:layout_marginBottom="8dp"/>
            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Deactivate CEO"
                android:backgroundTint="#33EF4444"
                android:textColor="@color/provider_error"
                android:layout_marginBottom="8dp"/>
        </LinearLayout>
    </ScrollView>
</LinearLayout>
"""

layout_create_ceo = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="@color/provider_surface_secondary">
        <ImageView
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@android:drawable/ic_menu_revert"
            android:padding="8dp"
            android:clickable="true"
            android:focusable="true"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Create CEO"
            android:textColor="@color/provider_text_primary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginStart="16dp"/>
    </LinearLayout>
    
    <LinearLayout
        android:id="@+id/successLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="24dp"
        android:visibility="gone">
        <TextView android:text="CEO CREATED" android:textColor="@color/provider_success" android:textSize="24sp" android:textStyle="bold"/>
        <TextView android:text="Account is ready" android:textColor="@color/provider_text_secondary" android:layout_marginTop="8dp"/>
        <Button android:id="@+id/btnViewCeo" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="VIEW CEO" android:layout_marginTop="24dp" android:backgroundTint="@color/provider_primary" android:textColor="@color/provider_background"/>
        <Button android:id="@+id/btnDone" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="DONE" android:layout_marginTop="8dp" android:backgroundTint="@color/provider_surface" android:textColor="@color/provider_text_primary"/>
    </LinearLayout>

    <ScrollView
        android:id="@+id/formLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">
            
            <!-- Dummy Form Fields -->
            <TextView android:text="PERSONAL INFORMATION" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Full Name" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Email" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Phone" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="32dp"/>
            
            <TextView android:text="ORGANIZATION" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Company Name" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Industry" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="32dp"/>
            
            <TextView android:text="ACCOUNT" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:layout_width="match_parent" android:layout_height="56dp" android:hint="Password" android:inputType="textPassword" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            
            <Button
                android:id="@+id/btnCreate"
                android:layout_width="match_parent"
                android:layout_height="56dp"
                android:text="CREATE CEO"
                android:backgroundTint="@color/provider_primary"
                android:textColor="@color/provider_background"
                android:layout_marginTop="24dp"
                android:layout_marginBottom="40dp"/>
        </LinearLayout>
    </ScrollView>
</LinearLayout>
"""

layout_profile = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/provider_background">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavProvider">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">
            
            <ImageView
                android:layout_width="100dp"
                android:layout_height="100dp"
                android:layout_gravity="center"
                android:src="@android:drawable/ic_menu_camera"
                android:background="@color/provider_surface"
                android:layout_marginTop="20dp"/>
                
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Provider Admin"
                android:textColor="@color/provider_text_primary"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_gravity="center"
                android:layout_marginTop="12dp"/>
                
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="admin@platform.com"
                android:textColor="@color/provider_text_secondary"
                android:textSize="16sp"
                android:layout_gravity="center"
                android:layout_marginTop="4dp"/>
                
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:layout_marginTop="32dp">
                
                <TextView android:text="ACCOUNT" android:textColor="@color/provider_text_muted" android:textStyle="bold" android:layout_marginBottom="8dp"/>
                <TextView android:text="Edit Profile" android:textColor="@color/provider_text_primary" android:padding="16dp" android:background="@color/provider_surface" android:layout_marginBottom="2dp"/>
                <TextView android:text="Change Password" android:textColor="@color/provider_text_primary" android:padding="16dp" android:background="@color/provider_surface" android:layout_marginBottom="2dp"/>
                <TextView android:id="@+id/btnSettings" android:text="Settings" android:textColor="@color/provider_text_primary" android:padding="16dp" android:background="@color/provider_surface" android:clickable="true" android:focusable="true"/>
                
                <TextView android:text="PLATFORM" android:textColor="@color/provider_text_muted" android:textStyle="bold" android:layout_marginTop="24dp" android:layout_marginBottom="8dp"/>
                <TextView android:text="About" android:textColor="@color/provider_text_primary" android:padding="16dp" android:background="@color/provider_surface" android:layout_marginBottom="2dp"/>
                <TextView android:text="Privacy &amp; Terms" android:textColor="@color/provider_text_primary" android:padding="16dp" android:background="@color/provider_surface"/>
                
                <Button
                    android:id="@+id/btnLogout"
                    android:layout_width="match_parent"
                    android:layout_height="56dp"
                    android:text="LOG OUT"
                    android:backgroundTint="#1AEF4444"
                    android:textColor="@color/provider_error"
                    android:layout_marginTop="32dp"
                    android:layout_marginBottom="40dp"/>
            </LinearLayout>
        </LinearLayout>
    </ScrollView>
    
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavProvider"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/provider_surface"
        app:itemIconTint="@color/nav_active"
        app:itemTextColor="@color/nav_active"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_provider" />
</androidx.constraintlayout.widget.ConstraintLayout>
"""

layout_generic = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/provider_background">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:orientation="vertical"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavProvider">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="{title}"
            android:textColor="@color/provider_text_primary"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_margin="24dp"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="This is a real screen (content coming here during integration)."
            android:textColor="@color/provider_text_secondary"
            android:layout_marginHorizontal="24dp"/>
    </LinearLayout>
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavProvider"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/provider_surface"
        app:itemIconTint="@color/nav_active"
        app:itemTextColor="@color/nav_active"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_provider" />
</androidx.constraintlayout.widget.ConstraintLayout>
"""

layout_generic_no_nav = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/provider_background">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="@color/provider_surface_secondary">
        <ImageView
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@android:drawable/ic_menu_revert"
            android:padding="8dp"
            android:clickable="true"
            android:focusable="true"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="{title}"
            android:textColor="@color/provider_text_primary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginStart="16dp"/>
    </LinearLayout>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="This is a real screen (content coming here during integration)."
        android:textColor="@color/provider_text_secondary"
        android:layout_margin="24dp"/>
</LinearLayout>
"""

layouts = {
    "activity_provider_home": layout_home,
    "activity_provider_ceo_list": layout_ceo_list,
    "activity_provider_ceo_details": layout_ceo_details,
    "activity_provider_create_ceo": layout_create_ceo,
    "activity_provider_workforce": layout_generic.replace("{title}", "Platform Workforce"),
    "activity_provider_analytics": layout_generic.replace("{title}", "Platform Analytics"),
    "activity_provider_alerts": layout_generic_no_nav.replace("{title}", "Alerts"),
    "activity_provider_profile": layout_profile,
    "activity_provider_settings": layout_generic_no_nav.replace("{title}", "Settings")
}

for filename, content in layouts.items():
    with open(os.path.join(layout_dir, f"{filename}.xml"), "w", encoding="utf-8") as f:
        f.write(content)

print("Layouts generated")
