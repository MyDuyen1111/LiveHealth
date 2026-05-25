import axios from 'axios';

// AI service uses its own base URL (proxied through Vite)
const aiClient = axios.create({ baseURL: '/api/ai' });

export const healthApi = {
    /** Full health analysis: BMI/BMR/TDEE + GPT advice + product recommendations */
    analyze: async ({ height, weight, age, gender, activityLevel }) => {
        const res = await aiClient.post('/analyze', {
            height: parseInt(height),
            weight: parseInt(weight),
            age: parseInt(age),
            gender: gender.toLowerCase(),
            activity_level: activityLevel.toLowerCase(),
        });
        return res.data;
    },

    /** Chat assistant: BMI flow + meal plan + shoppable products */
    chat: async ({ message, userProfile, planDays, goal, includeProducts = true }) => {
        const payload = {
            message: message || '',
            include_products: includeProducts,
        };

        if (userProfile && Object.keys(userProfile).length > 0) {
            payload.user_profile = userProfile;
        }
        if (planDays !== undefined && planDays !== null && String(planDays).trim() !== '') {
            const parsedPlanDays = parseInt(planDays, 10);
            if (!Number.isNaN(parsedPlanDays)) {
                payload.plan_days = parsedPlanDays;
            }
        }
        if (goal) {
            payload.goal = goal;
        }

        const res = await aiClient.post('/chat', payload);
        return res.data;
    },

    /** Health check */
    ping: async () => {
        const res = await aiClient.get('/health');
        return res.data;
    },
};
