package com.pypisan.sanchitra.presentation.screens.profile


import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pypisan.sanchitra.OnboardingActivity
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.clear
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class AccountsSectionData(
    val title: String,
    val value: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun AccountsSection(
    accountSectionViewModel: AccountSectionViewModel = hiltViewModel(),
    onLoadingChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val childPadding = rememberChildPadding()
    val scope = rememberCoroutineScope()
    val firstItemFocusRequester = remember { FocusRequester() }

    // Dialog & Process States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var logoutMessage by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf<String?>(null) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var changePasswordMessage by remember { mutableStateOf<String?>(null) }

    var showAddProfileDialog by remember { mutableStateOf(false) }
    var addProfileMessage by remember { mutableStateOf<String?>(null) }

    var showSwitchProfileDialog by remember { mutableStateOf(false) }

    val accountsSectionListItems = remember {
        listOf(
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionSwitchAccountsTitle,
                value = StringConstants.Profile.userProfileName,
                onClick = {
                    showSwitchProfileDialog = true
                }), AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionLogOut,
                value = StringConstants.Profile.accountsEmail,
                onClick = { showLogoutDialog = true }), AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordTitle,
                value = StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordValue,
                onClick = { showChangePasswordDialog = true }), AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionAddNewProfile,
                onClick = { showAddProfileDialog = true }), AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionRemoveProfile
            ), AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountTitle,
                onClick = { showDeleteDialog = true })
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = childPadding.start)
            .focusRestorer(firstItemFocusRequester)
            .focusGroup(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = accountsSectionListItems, key = { index, _ -> index }) { index, item ->
            AccountsSelectionItem(
                accountsSectionData = item, modifier = Modifier
                    .then(
                        if (index == 0) Modifier.focusRequester(firstItemFocusRequester)
                        else Modifier
                    )
                    .focusProperties {
                        if (index < 2) {
                            up = FocusRequester.Cancel
                        }
                    })
        }
    }

    // --- Delete Account Dialogs ---
    AccountSectionActionDialog(
        showDialog = showDeleteDialog,
        title = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountTitle,
        description = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountDescription,
        message = deleteMessage,
        confirmButtonText = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountConfirm,
        dismissButtonText = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountCancel,
        modifier = Modifier.width(600.dp),
        onDismissRequest = {
            showDeleteDialog = false
            deleteMessage = null
        },
        onConfirm = {
            showDeleteDialog = false
            onLoadingChange(true)

            scope.launch {
                val success = withContext(Dispatchers.IO) {
                    accountSectionViewModel.deleteAccount()
                }

                onLoadingChange(false)

                if (success) {
                    clear(context)
                    val intent = Intent(context, OnboardingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                } else {
                    deleteMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountFailed
                    showDeleteDialog = true
                }
            }
        })

//    Log Out dialog
    AccountSectionActionDialog(
        showDialog = showLogoutDialog,
        title = StringConstants.Composable.Placeholders.AccountsSelectionLogOut,
        description = StringConstants.Composable.Placeholders.AccountsSelectionLogOutDescription,
        message = logoutMessage,
        confirmButtonText = StringConstants.Composable.Placeholders.AccountsSelectionLogOut,
        dismissButtonText = StringConstants.Composable.Placeholders.AccountsSelectionLogOutCancel,
        modifier = Modifier.width(600.dp),
        onDismissRequest = {
            showLogoutDialog = false
            logoutMessage = null
        },
        onConfirm = {
            showLogoutDialog = false
            onLoadingChange(true)// Triggers full-screen loader

            scope.launch {
                val success = withContext(Dispatchers.IO) {
                    accountSectionViewModel.userProfileLogout()
                }

                onLoadingChange(false)

                if (success) {
                    clear(context)
                    val intent = Intent(context, OnboardingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                } else {
                    logoutMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionLogOutFailed
                    showLogoutDialog = true
                }
            }
        })

//    Change Password Dialog
    ChangePasswordDialog(
        showDialog = showChangePasswordDialog,
        message = changePasswordMessage,
        modifier = Modifier.width(600.dp),
        onDismissRequest = {
            showChangePasswordDialog = false
        },
        onConfirmChangePassword = { currentPass, newPass ->
            showChangePasswordDialog = false
            changePasswordMessage = null
            onLoadingChange(true)

            scope.launch {
                val success = withContext(Dispatchers.IO) {
                    accountSectionViewModel.userPasswordUpdate(currentPass, newPass)
                }

                onLoadingChange(false)

                if (success) {
                    // Password successfully changed
                    changePasswordMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordSuccess
                    showChangePasswordDialog = true
                } else {
                    // Show error on dialog with Close button
                    changePasswordMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordFailed
                    showChangePasswordDialog = true
                }
            }
        })

//    New Profile dialog
    NewProfileDialog(
        showDialog = showAddProfileDialog,
        message = addProfileMessage,
        modifier = Modifier,
        avatarUrls = listOf("https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/avatars/1.png",
            "https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/avatars/2.png"),
        onDismissRequest = {
            showAddProfileDialog = false
        },
        onConfirmAddProfile = { newProfileName, profilePicture ->
            showAddProfileDialog = false
            addProfileMessage = null
            onLoadingChange(true)

            scope.launch {
                val success = withContext(Dispatchers.IO) {
                    accountSectionViewModel.userAddNewProfile(newProfileName, profilePicture)
                }
                onLoadingChange(false)
                if (success) {
                    addProfileMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionAddNewProfileSuccess
                    showAddProfileDialog = true
                } else {
                    addProfileMessage =
                        StringConstants.Composable.Placeholders.AccountsSelectionAddNewProfileFailed
                    showAddProfileDialog = true
                }
            }
        })

//    Change Profile Dialog
    AccountSectionActionDialog(
        showDialog = showSwitchProfileDialog,
        title = StringConstants.Composable.Placeholders.AccountsSelectionSwitchAccountsTitle,
        description = StringConstants.Composable.Placeholders.AccountsSelectionSwitchAccountsMessage,
        confirmButtonText = StringConstants.Composable.Placeholders.Affirmative,
        dismissButtonText = StringConstants.Composable.Placeholders.Negative,
        modifier = Modifier.width(600.dp),
        onDismissRequest = {
            showSwitchProfileDialog = false
        },
        onConfirm = {
            showSwitchProfileDialog = false

            val intent = Intent(context, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("IS_SWITCH_PROFILE", true)
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        })
}
