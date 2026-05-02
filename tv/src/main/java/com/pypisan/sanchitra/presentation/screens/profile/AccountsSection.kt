package com.pypisan.sanchitra.presentation.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pypisan.sanchitra.data.repositories.AuthRepository
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class AccountsSectionData(
    val title: String,
    val value: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun AccountsSection() {

    val context = LocalContext.current
    val childPadding = rememberChildPadding()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var isLoggingOut by remember { mutableStateOf(false) }
    var logoutMessage by remember { mutableStateOf<String?>(null) }


    val accountsSectionListItems = remember {
        listOf(
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionSwitchAccountsTitle,
                value = StringConstants.Profile.accountsEmail
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionLogOut,
                value = StringConstants.Profile.accountsEmail,
                onClick = { showLogoutDialog = true }
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionChangePasswordTitle,
                value = StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordValue
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionAddNewAccountTitle,
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionViewSubscriptionsTitle
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountTitle,
                onClick = { showDeleteDialog = true }
            )
        )
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = childPadding.start),
        columns = GridCells.Fixed(2),
        content = {
            items(accountsSectionListItems.size) { index ->
                AccountsSelectionItem(
                    modifier = Modifier.focusRequester(focusRequester),
                    key = index,
                    accountsSectionData = accountsSectionListItems[index]
                )
            }
        }
    )

    AccountsSectionDeleteDialog(
        showDialog = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        modifier = Modifier.width(428.dp)
    )

    AccountLogoutDialog(
        showDialog = showLogoutDialog,
        isLoading = isLoggingOut,
        message = logoutMessage,
        modifier = Modifier.width(428.dp),
        onDismissRequest = {
            showLogoutDialog = false
            logoutMessage = null
        },
        onConfirmLogout = {
            isLoggingOut = true

            scope.launch {
                val repo = AuthRepository()

                val result = withContext(Dispatchers.IO) {
                    repo.userProfileLogout(context)
                }

                isLoggingOut = false

                if (result != null) {
                    logoutMessage = "Logout Successful"

                    delay(1200)

                    // SessionManager.clear(context)

                } else {
                    logoutMessage = "Logout Failed"

                    delay(1200)
                    logoutMessage = null
                    showLogoutDialog = false
                }
            }
        }
    )
}
