package com.example.laisa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laisa.entidades.ReviewedStudyCriteria;
import com.example.laisa.tcc.R;
import com.example.laisa.tcc.ReadSRFragment;

import java.util.ArrayList;

public class ReviewedStudyCriteriaAdapter extends ArrayAdapter<ReviewedStudyCriteria>{

    private ArrayList<ReviewedStudyCriteria> criteriaList;

    public ReviewedStudyCriteriaAdapter(Context context, int textViewResourceId,
                           ArrayList<ReviewedStudyCriteria> criteriaList) {
        super(context, textViewResourceId, criteriaList);
        this.criteriaList = criteriaList;
    }

    private class ViewHolder {
        TextView criteriaName;
        CheckBox criteriaCheckbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.inclusion_list_layout, null);

            holder = new ViewHolder();
            holder.criteriaName = (TextView) convertView.findViewById(R.id.listCriteriaName);
            holder.criteriaCheckbox = (CheckBox) convertView.findViewById(R.id.listCriteriaCheckbox);
            convertView.setTag(holder);
            holder.criteriaCheckbox.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    if(v instanceof CheckBox) {
                        CheckBox cb = (CheckBox) v;
                        ReviewedStudyCriteria criteria = (ReviewedStudyCriteria) cb.getTag();
                        criteria.setSatisfied(cb.isChecked());
                    }
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReviewedStudyCriteria criteria = criteriaList.get(position);
        holder.criteriaName.setText(criteria.getCriteria().getDescription());
        holder.criteriaCheckbox.setChecked(criteria.isSatisfied());
        holder.criteriaCheckbox.setTag(criteria);

        return convertView;

    }
}
