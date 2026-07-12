import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import {
  useVehicleDetail,
  useCreateVehicle,
  useUpdateVehicle,
} from '../features/vehicles/hooks/useVehicles';
import vehicleService from '../features/vehicles/services/vehicleService';
import VehicleForm from '../features/vehicles/components/VehicleForm';
import ImageUpload from '../features/vehicles/components/ImageUpload';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import toast from 'react-hot-toast';

function CreateVehiclePage() {
  const navigate = useNavigate();
  const createVehicle = useCreateVehicle();

  const handleSubmit = async (data) => {
    try {
      await createVehicle.mutateAsync(data);
      toast.success('Vehicle created successfully');
      navigate('/vehicles');
    } catch (err) {
      toast.error(err.message || 'Failed to create vehicle');
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <button
        type="button"
        onClick={() => navigate(-1)}
        className="flex items-center gap-1.5 mb-6 text-sm font-medium text-text-secondary hover:text-primary transition-colors cursor-pointer"
      >
        <ArrowLeft size={16} />
        Back
      </button>
      <div className="bg-surface border border-border rounded-2xl p-6 sm:p-8">
        <h1 className="text-2xl font-bold text-text-primary mb-6">Add New Vehicle</h1>
        <VehicleForm
          onSubmit={handleSubmit}
          onCancel={() => navigate('/vehicles')}
          isSubmitting={createVehicle.isPending}
        />
      </div>
    </div>
  );
}

function EditVehiclePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data: vehicle, isLoading, error } = useVehicleDetail(id);
  const updateVehicle = useUpdateVehicle();
  const [imageFile, setImageFile] = useState(null);

  const handleSubmit = async (data) => {
    try {
      await updateVehicle.mutateAsync({ id, data });
      if (imageFile) {
        await vehicleService.uploadImage(id, imageFile);
      }
      toast.success('Vehicle updated successfully');
      navigate('/vehicles');
    } catch (err) {
      toast.error(err.message || 'Failed to update vehicle');
    }
  };

  if (isLoading)
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  if (error) return <ErrorDisplay message={error.message} />;
  if (!vehicle) return null;

  return (
    <div className="max-w-2xl mx-auto">
      <button
        type="button"
        onClick={() => navigate(-1)}
        className="flex items-center gap-1.5 mb-6 text-sm font-medium text-text-secondary hover:text-primary transition-colors cursor-pointer"
      >
        <ArrowLeft size={16} />
        Back
      </button>
      <div className="bg-surface border border-border rounded-2xl p-6 sm:p-8">
        <h1 className="text-2xl font-bold text-text-primary mb-6">Edit Vehicle</h1>
        <div className="space-y-6">
          <ImageUpload
            currentImageUrl={vehicle.imageUrl}
            onFileSelect={setImageFile}
            onRemove={() => setImageFile(null)}
          />
          <VehicleForm
            vehicle={vehicle}
            onSubmit={handleSubmit}
            onCancel={() => navigate('/vehicles')}
            isSubmitting={updateVehicle.isPending}
          />
        </div>
      </div>
    </div>
  );
}

function VehicleFormPage() {
  const { id } = useParams();
  return id ? <EditVehiclePage /> : <CreateVehiclePage />;
}

export default VehicleFormPage;
export { CreateVehiclePage, EditVehiclePage };
