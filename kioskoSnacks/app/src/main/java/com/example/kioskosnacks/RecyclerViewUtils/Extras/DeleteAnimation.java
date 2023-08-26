package com.example.kioskosnacks.RecyclerViewUtils.Extras;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class DeleteAnimation extends DefaultItemAnimator {
    private static final long DURATION = 400; // Duración de la animación en milisegundos (0.4 segundos)

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        Animator animator = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth());
        animator.setDuration(DURATION);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dispatchRemoveFinished(holder);
                view.setTranslationX(0); // Restaurar la posición original después de la animación
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
        return false;
    }
}
