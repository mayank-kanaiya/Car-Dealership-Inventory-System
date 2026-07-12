import PropTypes from 'prop-types';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import Input from '../../../components/Input/Input';
import Button from '../../../components/Button/Button';
import { VEHICLE_CATEGORIES } from '../../../constants/vehicleCategories';

const vehicleSchema = yup.object({
  make: yup.string().max(80, 'Make must be 80 characters or less').required('Make is required'),
  model: yup.string().max(80, 'Model must be 80 characters or less').required('Model is required'),
  category: yup.string().oneOf(VEHICLE_CATEGORIES.map((c) => c.value), 'Invalid category').required('Category is required'),
  price: yup.number().positive('Price must be positive').required('Price is required'),
  quantityInStock: yup.number().integer('Must be a whole number').min(0, 'Cannot be negative').required('Stock is required'),
});

function VehicleForm({ vehicle, onSubmit, onCancel, isSubmitting = false }) {
  const isEditing = !!vehicle;

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(vehicleSchema),
    defaultValues: vehicle
      ? { make: vehicle.make, model: vehicle.model, category: vehicle.category, price: vehicle.price, quantityInStock: vehicle.quantityInStock }
      : { make: '', model: '', category: '', price: '', quantityInStock: '' },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4" noValidate>
      <Input
        label="Make"
        {...register('make')}
        error={errors.make?.message}
        placeholder="e.g. Toyota"
      />
      <Input
        label="Model"
        {...register('model')}
        error={errors.model?.message}
        placeholder="e.g. Camry"
      />
      <div>
        <label htmlFor="category" className="block text-sm font-medium text-text-secondary mb-1">Category</label>
        <select
          id="category"
          {...register('category')}
          className="w-full px-3 py-2 text-sm border rounded-lg bg-surface text-text-primary focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
          style={{ borderColor: errors.category ? '#ef4444' : undefined }}
        >
          <option value="">Select a category</option>
          {VEHICLE_CATEGORIES.map((cat) => (
            <option key={cat.value} value={cat.value}>{cat.label}</option>
          ))}
        </select>
        {errors.category?.message && <p className="text-xs text-danger mt-1">{errors.category.message}</p>}
      </div>
      <Input
        label="Price"
        type="number"
        step="0.01"
        {...register('price')}
        error={errors.price?.message}
        placeholder="e.g. 28500"
      />
      <Input
        label="Quantity in Stock"
        type="number"
        {...register('quantityInStock')}
        error={errors.quantityInStock?.message}
        placeholder="e.g. 10"
      />
      <div className="flex gap-3 pt-2">
        <Button type="submit" variant="primary" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : isEditing ? 'Update Vehicle' : 'Create Vehicle'}
        </Button>
        <Button type="button" variant="ghost" onClick={onCancel} disabled={isSubmitting}>
          Cancel
        </Button>
      </div>
    </form>
  );
}

VehicleForm.propTypes = {
  vehicle: PropTypes.shape({
    id: PropTypes.string,
    make: PropTypes.string,
    model: PropTypes.string,
    category: PropTypes.string,
    price: PropTypes.number,
    quantityInStock: PropTypes.number,
  }),
  onSubmit: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool,
};

export default VehicleForm;
