@file:OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)

package com.ratik.uttam.ui.feature.onboarding

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ratik.uttam.R
import com.ratik.uttam.bg.RefreshWallpaperWorker
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.ui.components.UttamText
import com.ratik.uttam.ui.components.VerticalSpacer
import com.ratik.uttam.ui.extensions.collectAsEffect
import com.ratik.uttam.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.FinishOnboarding
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.NotificationPermissionResponded
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.ShowNextStep
import com.ratik.uttam.ui.feature.onboarding.OnboardingNavTarget.Home
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.DONE
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.FULL_CONTROL
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.NOTIF_PERMISSION
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.WELCOME
import com.ratik.uttam.ui.feature.onboarding.model.requiresNotificationPermission
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.Dimens.IconXXXXSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingXXSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXXSmall
import com.ratik.uttam.ui.theme.OnboardingBackground
import com.ratik.uttam.ui.theme.setStatusBarColors
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

@SuppressLint("InlinedApi")
@Composable
internal fun OnboardingScreen(
  viewModel: OnboardingViewModel = hiltViewModel(),
  navigateToHome: () -> Unit,
) {
  setStatusBarColors(isDarkIcons = false)

  val context = LocalContext.current
  val displayMetrics = context.resources.displayMetrics

  val state by
    rememberFlowOnLifecycle(flow = viewModel.state).collectAsState(OnboardingState.initialState)

  val pagerState = rememberPagerState(pageCount = { state.onboardingSteps.size })

  val coroutineScope = rememberCoroutineScope()

  val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

  val notificationsPermissionState =
    rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS) {
      viewModel.onViewAction(NotificationPermissionResponded)
      coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
    }

  viewModel.events.collectAsEffect { event ->
    when (event) {
      is Navigate -> {
        when (event.target) {
          is Home -> navigateToHome()
        }
      }
      else -> Ignored
    }
  }

  Column(
    modifier =
    Modifier.fillMaxSize()
      .background(OnboardingBackground)
      .navigationBarsPadding()
      .padding(horizontal = SpacingLarge),
    verticalArrangement = SpaceBetween,
  ) {
    HorizontalPager(
      state = pagerState,
      userScrollEnabled = false,
      modifier = Modifier.weight(1F),
    ) { page ->
      OnboardingContent(state.onboardingSteps[page])
    }
    VerticalSpacer(SpacingXXXLarge)
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
      PageIndicator(pagerState = pagerState, modifier = Modifier.align(Center))
      if (isLastPage) {
        UttamText.Body(
          text = "Done",
          modifier =
          Modifier.align(CenterEnd).padding(vertical = SpacingXXSmall).clickable {
            // enqueueDailyRefreshRequest(context)

            viewModel.onViewAction(
              FinishOnboarding(
                deviceHeight = displayMetrics.heightPixels,
                deviceWidth = displayMetrics.widthPixels,
              ),
            )
          },
        )
      } else {
        Icon(
          painter = painterResource(id = R.drawable.ic_arrow_right),
          contentDescription = stringResource(R.string.content_desc_right_arrow),
          tint = White,
          modifier =
          Modifier.align(CenterEnd)
            .padding(vertical = SpacingXXXSmall, horizontal = SpacingSmall)
            .clickable {
              val currentStep = state.onboardingSteps[state.currentStepIndex]
              if (currentStep.requiresNotificationPermission()) {
                notificationsPermissionState.launchPermissionRequest()
              } else {
                viewModel.onViewAction(ShowNextStep)
                coroutineScope.launch {
                  pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
              }
            },
        )
      }
    }
    VerticalSpacer(SpacingLarge)
  }
}

@Composable
private fun PageIndicator(modifier: Modifier = Modifier, pagerState: PagerState) {
  Row(
    modifier = modifier,
    verticalAlignment = CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    repeat(pagerState.pageCount) { iteration ->
      val color = if (pagerState.currentPage == iteration) Color.Black else ColorPrimaryVariant
      Box(
        modifier =
        Modifier.padding(SpacingXXXXXSmall)
          .clip(CircleShape)
          .background(color)
          .size(IconXXXXSmall),
      )
    }
  }
}

@Composable
private fun OnboardingContent(onboardingStep: OnboardingStep) {
  val context = LocalContext.current

  Box(modifier = Modifier.fillMaxHeight()) {
    Image(
      painter = painterResource(id = getOnboardingImage(onboardingStep)),
      contentDescription = null,
      modifier = Modifier.align(Center),
    )
    Column(modifier = Modifier.align(BottomCenter)) {
      UttamText.BodyBold(text = getOnboardingTitle(context, onboardingStep), textColor = White)
      VerticalSpacer(SpacingSmall)
      UttamText.Body(text = getOnboardingMessage(context, onboardingStep), textColor = White)
    }
  }
}

private fun getOnboardingImage(onboardingStep: OnboardingStep): Int {
  return when (onboardingStep) {
    WELCOME -> R.drawable.tour_graphic_1
    NOTIF_PERMISSION -> R.drawable.tour_graphic_2
    FULL_CONTROL -> R.drawable.tour_graphic_3
    DONE -> R.drawable.tour_graphic_3
  }
}

private fun getOnboardingTitle(context: Context, onboardingStep: OnboardingStep): String {
  return when (onboardingStep) {
    WELCOME -> context.getString(R.string.tour_slide_1_heading)
    NOTIF_PERMISSION -> context.getString(R.string.tour_slide_2_heading)
    FULL_CONTROL -> context.getString(R.string.tour_slide_3_heading)
    DONE -> ""
  }
}

private fun getOnboardingMessage(context: Context, onboardingStep: OnboardingStep): String {
  return when (onboardingStep) {
    WELCOME -> context.getString(R.string.tour_slide_1_text)
    NOTIF_PERMISSION -> context.getString(R.string.tour_slide_2_text)
    FULL_CONTROL -> context.getString(R.string.tour_slide_3_text)
    DONE -> context.getString(R.string.all_done_text)
  }
}

private fun enqueueDailyRefreshRequest(context: Context) {
  val repeatInterval = TimeUnit.HOURS.toMillis(5)
  val initialDelay = calculateInitialDelayToMorning()

  val workRequest =
    PeriodicWorkRequest.Builder(RefreshWallpaperWorker::class.java, repeatInterval, MILLISECONDS)
      .setInitialDelay(initialDelay, MILLISECONDS)
      .build()

  WorkManager.getInstance(context)
    .enqueueUniquePeriodicWork(
      RefreshWallpaperWorker::class.java.simpleName,
      ExistingPeriodicWorkPolicy.UPDATE,
      workRequest,
    )
}

private fun calculateInitialDelayToMorning(): Long {
  val now = Calendar.getInstance()

  // Set the target time to 7 AM today
  val target =
    Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 7)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

  // If the target time is before now, set it to 7 AM tomorrow
  if (target.before(now)) {
    target.add(Calendar.DAY_OF_YEAR, 1)
  }

  // Calculate the delay in milliseconds
  return target.timeInMillis - now.timeInMillis
}
