package widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import me.lakshay.eventsguide.R;

public class CategoriesWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			Intent categoriesWidgetServiceIntent =
					new Intent(context, CategoriesWidgetService.class);
			categoriesWidgetServiceIntent.putExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			categoriesWidgetServiceIntent.setData(
					Uri.parse(categoriesWidgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.categories_app_widget);
			remoteViews.setRemoteAdapter(R.id.categories_list_view,
					categoriesWidgetServiceIntent);

			/*Intent navigationActivityIntent = new Intent(context,
					NavigationActivity.class);
			navigationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			navigationActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			navigationActivityIntent.setData(Uri.parse(
					navigationActivityIntent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent navigationActivityPendingIntent = PendingIntent.getActivity(
					context, 0, navigationActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.categories_list_view,
					navigationActivityPendingIntent);*/

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

}
