package com.ratik.uttam.ui.onboarding

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fueled.android.core.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.VerticalSpacer
import com.ratik.uttam.ui.onboarding.model.OnboardingStep
import com.ratik.uttam.ui.onboarding.model.OnboardingStep.DONE
import com.ratik.uttam.ui.onboarding.model.OnboardingStep.FULL_CONTROL
import com.ratik.uttam.ui.onboarding.model.OnboardingStep.NOTIF_PERMISSION
import com.ratik.uttam.ui.onboarding.model.OnboardingStep.WELCOME
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.Dimens.IconXXXXSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingMedium
import com.ratik.uttam.ui.theme.Dimens.SpacingSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXSmall
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXXSmall
import com.ratik.uttam.ui.theme.setStatusBarColors

@Composable
internal fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
) {
    setStatusBarColors(isDarkIcons = false)

    val state by rememberFlowOnLifecycle(flow = viewModel.state).collectAsState(OnboardingState.initialState)

    val pagerState = rememberPagerState(pageCount = { state.onboardingSteps.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
            .navigationBarsPadding()
            .padding(horizontal = SpacingMedium),
        verticalArrangement = SpaceBetween,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1F)
        ) { page ->
            OnboardingContent(state.onboardingSteps[page])
        }
        VerticalSpacer(SpacingXXXLarge)
        PageIndicator(pagerState = pagerState)
        VerticalSpacer(SpacingLarge)
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
) {
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = SpacingXXXSmall),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) Color.DarkGray else ColorPrimaryVariant
            Box(
                modifier = Modifier
                    .padding(SpacingXXXXXSmall)
                    .clip(CircleShape)
                    .background(color)
                    .size(IconXXXXSmall)
            )
        }
    }
}

@Composable
private fun OnboardingContent(
    onboardingStep: OnboardingStep
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxHeight(),
    ) {
        Image(
            painter = painterResource(id = getOnboardingImage(onboardingStep)),
            contentDescription = null,
            modifier = Modifier.align(Center),
        )
        Column(
            modifier = Modifier
                .align(BottomCenter),
        ) {
            Text(
                text = getOnboardingTitle(context, onboardingStep),
                color = Color.White
            )
            VerticalSpacer(SpacingSmall)
            Text(
                text = getOnboardingMessage(context, onboardingStep),
                color = Color.White
            )
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
        DONE -> context.getString(R.string.all_done_text)
    }
}

private fun getOnboardingMessage(context: Context, onboardingStep: OnboardingStep): String {
    return when (onboardingStep) {
        WELCOME -> context.getString(R.string.tour_slide_1_text)
        NOTIF_PERMISSION -> context.getString(R.string.tour_slide_2_text)
        FULL_CONTROL -> context.getString(R.string.tour_slide_3_text)
        DONE -> ""
    }
}