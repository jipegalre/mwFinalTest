from django.urls import path
from . import views
from django.urls import include
from rest_framework import routers

router = routers.DefaultRouter()
router1 = routers.DefaultRouter()
router2 = routers.DefaultRouter()
router.register('Post', views.blogImage)
router1.register('CheckAttendance', views.checkAttendance)
router2.register('CheckedAttendance', views.checkedAttendance)


urlpatterns = [
    path('', views.post_list, name='post_list'),
    path('post/<int:pk>/', views.post_detail, name='post_detail'),
    path('post/new/', views.post_new, name='post_new'),
    path('post/<int:pk>/edit/', views.post_edit, name='post_edit'),
    path('api_root/', include(router.urls)),
    path('api_root/', include(router1.urls)),
    path('api_root/', include(router2.urls)),

]
