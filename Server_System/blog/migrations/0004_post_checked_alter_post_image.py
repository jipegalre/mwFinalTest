# Generated by Django 5.0.5 on 2024-12-14 05:27

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0003_alter_post_image'),
    ]

    operations = [
        migrations.AddField(
            model_name='post',
            name='checked',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='post',
            name='image',
            field=models.ImageField(default='default_error.png', upload_to='blog_image/%Y/%m/%d/'),
        ),
    ]